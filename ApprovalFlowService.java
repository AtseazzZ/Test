package com.tt.workflow;

import com.tt.entity.ApprovalFlowConfig;
import com.tt.entity.ApprovalRecord;
import com.tt.entity.Contract;
import com.tt.mapper.ApprovalFlowConfigMapper;
import com.tt.mapper.ApprovalRecordMapper;
import com.tt.mapper.ContractMapper;
import com.tt.audit.AuditLogService;
import com.tt.permission.DataScopeResolver;
import com.tt.permission.DataScopeResult;
import com.tt.permission.FunctionPermissionResolver;
import com.tt.common.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 审批流服务（状态机 + 节点门控）
 *
 * 核心设计：
 * 1. 状态机配置化：状态转移规则存在 approval_flow_config 表，数据驱动
 * 2. 双重门控：
 *    - 功能权限门控：hasPermission(user, requiredPermission)
 *    - 状态机门控：contract.status == config.fromStatus
 * 3. 节点顺序强制：通过状态机转移规则强制，非 if-else
 *    - U3 审批 PENDING_ADMIN 状态的合同 → 查不到 APPROVE/PENDING_ADMIN 的配置 → 抛异常
 * 4. 数据范围门控：合同不在用户数据范围内 → 查不到 → 404
 *
 * 不能越级/跳节点的保证：
 * - 每个操作只能从特定的 from_status 执行
 * - 不在对应节点的合同，flowConfigMapper.selectByFromStatusAndAction 返回 null
 * - 直接抛出 BusinessException，前端展示错误信息
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalFlowService {

    private final ContractMapper contractMapper;
    private final ApprovalFlowConfigMapper flowConfigMapper;
    private final ApprovalRecordMapper approvalRecordMapper;
    private final FunctionPermissionResolver functionPermissionResolver;
    private final DataScopeResolver dataScopeResolver;
    private final AuditLogService auditLogService;

    /**
     * 执行审批操作（统一入口）
     *
     * @param userId     当前用户ID
     * @param contractId 合同ID
     * @param action     操作（SUBMIT/PROCESS/APPROVE/WITHDRAW/DELETE）
     * @param comment    审批意见
     */
    @Transactional
    public void executeAction(Long userId, String username, Long contractId,
                              String action, String comment) {
        // 1. 数据范围解析
        DataScopeResult scope = dataScopeResolver.resolveScope(userId);

        // 2. 查合同（施加数据范围，越权访问返回 404）
        Contract contract = contractMapper.selectByIdWithScope(contractId, scope.getSqlCondition());
        if (contract == null) {
            // 数据范围外的合同 → 404（不泄露存在性）
            auditLogService.logFailure(action, "CONTRACT", contractId, "合同不存在或无权访问");
            throw new BusinessException(404, "合同不存在或无权访问");
        }

        // 3. 状态机门控：查状态转移配置
        ApprovalFlowConfig config = flowConfigMapper.selectByFromStatusAndAction(contract.getStatus(), action);
        if (config == null) {
            // 当前状态不允许此操作 → 节点顺序违规
            auditLogService.logFailure(action, "CONTRACT", contractId,
                    "状态机门控失败: 当前状态=" + contract.getStatus() + ", 操作=" + action);
            throw new Busines sException(403, "当前合同状态[" + contract.getStatus() + "]不允许执行[" + action + "]操作");
        }

        // 4. 功能权限门控
        if (!functionPermissionResolver.hasPermission(userId, config.getRequiredPermission())) {
            auditLogService.logFailure(action, "CONTRACT", contractId,
                    "功能权限不足: 需要" + config.getRequiredPermission());
            throw new BusinessException(403, "无操作权限: 需要" + config.getRequiredPermission());
        }

        // 5. 节点角色门控（可选，双重保险）
        if (config.getRequiredRoleCode() != null && !config.getRequiredRoleCode().isEmpty()) {
            java.util.List<String> roles = functionPermissionResolver.getUserRoleCodes(userId);
            // 检查用户是否有该角色（含继承：领导继承部门经理）
            if (!hasRoleOrInherited(roles, config.getRequiredRoleCode())) {
                auditLogService.logFailure(action, "CONTRACT", contractId,
                        "角色门控失败: 需要" + config.getRequiredRoleCode());
                throw new BusinessException(403, "当前节点需要[" + config.getRequiredRoleCode() + "]角色");
            }
        }

        // 6. 执行状态转移
        String fromStatus = contract.getStatus();
        String toStatus = config.getToStatus();
        contract.setStatus(toStatus);
        contractMapper.updateById(contract);

        // 7. 记录审批记录
        ApprovalRecord record = new ApprovalRecord();
        record.setContractId(contractId);
        record.setFromStatus(fromStatus);
        record.setToStatus(toStatus);
        record.setAction(action);
        record.setOperatorId(userId);
        record.setOperatorName(username);
        record.setComment(comment);
        record.setCreateTime(LocalDateTime.now());
        approvalRecordMapper.insert(record);

        // 8. 记录审计日志
        auditLogService.logSuccess(action, "CONTRACT", contractId,
                String.format("%s 将合同从 %s 推进到 %s", username, fromStatus, toStatus));

        log.info("审批操作完成: user={}, contract={}, action={}, {}→{}",
                username, contractId, action, fromStatus, toStatus);
    }

    /**
     * 检查用户是否有某角色（精确匹配，不含继承）
     *
     * 设计说明：
     * - 功能权限轴：允许角色继承（LEADER 继承 DEPT_MANAGER 的 contract:approve 权限）
     * - 审批节点门控：不允许继承，必须精确匹配角色
     *   原因：若领导能替部门经理审批，则节点顺序失效（U4 可跳过 U3 直接审批）
     */
    private boolean hasRoleOrInherited(java.util.List<String> userRoles, String requiredRole) {
        return userRoles.contains(requiredRole);
    }
}
