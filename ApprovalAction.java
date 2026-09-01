package com.tt.workflow;

/**
 * 审批操作枚举
 */
public enum ApprovalAction {
    SUBMIT,    // 提交（申请人）
    PROCESS,   // 处理（合同管理员）
    APPROVE,   // 审批（部门经理/领导）
    WITHDRAW,  // 撤回（申请人）
    DELETE     // 删除（领导）
}
