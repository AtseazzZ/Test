package com.zczt.controller;

import com.zczt.constants.CommonConstants;
import com.zczt.pojo.AccountsReceivableWriteOff;
import com.zczt.pojo.Result;
import com.zczt.pojo.dto.AccountsReceivableWriteOffCreateDto;
import com.zczt.pojo.dto.AccountsReceivableWriteOffUpdateDto;
import com.zczt.service.AccountsReceivableWriteOffService;
import com.zczt.utils.UserContextUtil;
import com.zczt.pojo.User;
import com.zczt.pojo.ApprovalTask;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应收冲销Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/accounts-receivable-write-off")
@RequiredArgsConstructor
public class AccountsReceivableWriteOffController {

    private final AccountsReceivableWriteOffService writeOffService;
    private final UserContextUtil userContextUtil;

    @PostMapping
    public Result<AccountsReceivableWriteOff> createWriteOffApproval(@Valid @RequestBody AccountsReceivableWriteOffCreateDto dto) {
        log.info("接收到创建应收冲销审批请求，发票ID: {}", dto.getInvoiceId());

        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("无法获取当前用户信息");
        }
        
        dto.setSubmitterId(currentUser.getId());
        dto.setSubmitterName(currentUser.getName());
        
        AccountsReceivableWriteOff result = writeOffService.createWriteOffApproval(dto);
        
        log.info("应收冲销审批创建成功，ID: {}", result.getId());
        return Result.success(result);
    }

    @PutMapping("/{writeOffId}")
    public Result<String> updateWriteOffApproval(@PathVariable @Valid Integer writeOffId, @Valid @RequestBody AccountsReceivableWriteOffUpdateDto dto) {
        log.info("接收到创建应收冲销审批请求，发票ID: {}", dto.getInvoiceId());

        try {
            User currentUser = userContextUtil.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("无法获取当前用户信息");
            }

            dto.setSubmitterId(currentUser.getId());
            dto.setSubmitterName(currentUser.getName());

            writeOffService.updateWriteOff(writeOffId,dto);

            log.info("应收冲销记录更新成功，应收冲销ID: {}", writeOffId);
            return Result.success("更新成功");
        }catch (Exception e) {
            log.error("应收冲销记录更新失败，应收冲销ID: {}", writeOffId, e);
            return Result.error("应收冲销记录更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 重置草稿中应收冲销的流水
     *
     * @param writeOffId 应收冲销ID
     * @return 操作结果
     */
    @PutMapping("/{writeOffId}/reset-flow")
    public Result<String> resetWriteOffReconciliationFlow(@PathVariable @NotNull Integer writeOffId) {
        try {
            writeOffService.resetWriteOffReconciliationFlow(writeOffId);
            log.info("重置应收冲销流水成功，应收冲销ID: {}", writeOffId);
            return Result.success("重置应收冲销流水成功");
        } catch (Exception e) {
            log.error("重置应收冲销流水失败，应收冲销ID: {}", writeOffId, e);
            return Result.error("重置应收冲销流水失败: " + e.getMessage());
        }
    }
    
    /**
     * 审批通过应收冲销任务
     * @param taskId 任务ID
     * @param doneMsg 审批意见
     * @return 审批结果
     */
    @PostMapping("/approval/approve/{taskId}")
    public Result<Boolean> approveTask(@PathVariable @NotNull Integer taskId, @RequestParam(required = false) String doneMsg) {
        log.info("审批通过应收冲销任务, 任务ID: {}, 审批意见: {}", taskId, doneMsg);
        
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            return Result.error("无法获取当前用户信息");
        }

        // 处理审批任务
        boolean result = writeOffService.processTask(taskId, CommonConstants.ApprovalResult.APPROVED, doneMsg, currentUser.getId());
        return Result.success(result);
    }
    
    /**
     * 审批否决应收冲销任务
     * @param taskId 任务ID
     * @param doneMsg 审批意见
     * @return 审批结果
     */
    @PostMapping("/approval/reject/{taskId}")
    public Result<Boolean> rejectTask(@PathVariable @NotNull Integer taskId, @RequestParam(required = false) String doneMsg) {
        log.info("审批否决应收冲销任务, 任务ID: {}, 审批意见: {}", taskId, doneMsg);
        
        User currentUser = userContextUtil.getCurrentUser();
        if (currentUser == null) {
            return Result.error("无法获取当前用户信息");
        }

        // 处理审批任务
        boolean result = writeOffService.processTask(taskId, CommonConstants.ApprovalResult.REJECTED, doneMsg, currentUser.getId());
        return Result.success(result);
    }

    /**
     * 获取应收冲销审批任务列表
     * @param writeOffId 应收冲销ID
     * @return 审批任务列表
     */
    @GetMapping("/{writeOffId}/tasks")
    public Result<List<ApprovalTask>> getInvoiceTasks(
            @PathVariable @NotNull @Positive Integer writeOffId) {
        log.info("查询应收冲销审批任务：writeOffId={}", writeOffId);

        try {
            List<ApprovalTask> tasks = writeOffService.getSortedTasksById(writeOffId);
            log.info("查询应收冲销审批任务成功：writeOffId={}, 任务数量={}", writeOffId, tasks.size());

            return Result.success("查询审批任务成功", tasks);
        } catch (Exception e) {
            log.error("查询应收冲销审批任务失败", e);
            return Result.error("查询审批任务失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据应收冲销ID获取应收冲销详情、发票信息和钩稽流水
     * 
     * @param writeOffId 应收冲销ID
     * @return 应收冲销详情（包含关联的发票信息和钩稽流水）
     */
    @GetMapping("/{writeOffId}/details")
    public Result<AccountsReceivableWriteOff> getWriteOffDetails(
            @PathVariable @NotNull @Positive Integer writeOffId) {
        log.info("接收到获取应收冲销详情请求，应收冲销ID: {}", writeOffId);
        
        try {
            // 获取当前用户信息（用于日志记录）
            User user = userContextUtil.getCurrentUser();
            if (user == null || user.getId() == null) {
                log.warn("用户未登录或用户信息获取失败");
                return Result.error("用户未登录");
            }

            // 调用服务层获取应收冲销详情
            AccountsReceivableWriteOff writeOffDetails = writeOffService.getWriteOffWithDetails(writeOffId);
            
            log.info("成功获取应收冲销详情，应收冲销ID: {}, 操作人: {}", writeOffId, user.getName());
            return Result.success(writeOffDetails);
            
        } catch (Exception e) {
            log.error("获取应收冲销详情失败，应收冲销ID: {}", writeOffId, e);
            return Result.error("获取应收冲销详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 取消应收冲销审批
     * 注意：此接口会先调用重置流水方法，然后删除审批任务和冲销记录
     * 
     * @param writeOffId 应收冲销ID
     * @return 操作结果
     */
    @PostMapping("/{writeOffId}/cancel")
    public Result<String> cancelWriteOffApproval(
            @PathVariable @NotNull @Positive Integer writeOffId) {
        log.info("取消应收冲销审批，应收冲销ID: {}", writeOffId);
        
        try {
            // 获取当前用户信息
            User user = userContextUtil.getCurrentUser();
            if (user == null || user.getId() == null) {
                log.warn("用户未登录或用户信息获取失败");
                return Result.error("用户未登录");
            }
            
            // 调用服务层取消应收冲销审批
            boolean success = writeOffService.cancelWriteOffApproval(writeOffId);
            
            if (success) {
                log.info("应收冲销审批取消成功，应收冲销ID: {}, 操作人: {}", writeOffId, user.getName());
                return Result.success("应收冲销审批取消成功");
            } else {
                log.error("应收冲销审批取消失败，应收冲销ID: {}", writeOffId);
                return Result.error("应收冲销审批取消失败");
            }
            
        } catch (Exception e) {
            log.error("取消应收冲销审批失败，应收冲销ID: {}", writeOffId, e);
            return Result.error("取消应收冲销审批失败: " + e.getMessage());
        }
    }

}