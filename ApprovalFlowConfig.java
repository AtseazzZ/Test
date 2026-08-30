package com.tt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("approval_flow_config")
public class ApprovalFlowConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String fromStatus;
    private String toStatus;
    private String action;
    private String requiredPermission;
    private String requiredRoleCode;
    private Integer sortOrder;
}
