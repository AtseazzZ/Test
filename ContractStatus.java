package com.tt.workflow;

/**
 * 合同审批状态枚举
 *
 * 状态机：
 * DRAFT → PENDING_ADMIN → PENDING_MANAGER → PENDING_LEADER → APPROVED
 *                                              ↓
 *                                         DELETED (领导删除)
 * DRAFT → WITHDRAWN (申请人撤回)
 */
public enum ContractStatus {
    DRAFT,              // 草稿
    PENDING_ADMIN,      // 待合同管理员处理
    PENDING_MANAGER,    // 待部门经理审批
    PENDING_LEADER,     // 待领导审批
    APPROVED,           // 已通过（终态）
    WITHDRAWN,          // 已撤回（终态）
    DELETED;            // 已删除（终态）

    public boolean isTerminal() {
        return this == APPROVED || this == WITHDRAWN || this == DELETED;
    }
}
