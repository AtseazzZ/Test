package com.tt.vo;

import lombok.Data;
import java.util.List;

@Data
public class ApprovalRecordVO {
    private Long id;
    private Long contractId;
    private String fromStatus;
    private String toStatus;
    private String action;
    private Long operatorId;
    private String operatorName;
    private String comment;
    private String createTime;
}
