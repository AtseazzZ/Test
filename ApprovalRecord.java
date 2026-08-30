package com.tt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("approval_record")
public class ApprovalRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long contractId;
    private String fromStatus;
    private String toStatus;
    private String action;
    private Long operatorId;
    private String operatorName;
    private String comment;
    private LocalDateTime createTime;
}
