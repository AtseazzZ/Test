package com.tt.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractVO {
    private Long id;
    private String contractNo;
    private String name;
    private String customer;
    private BigDecimal amount;
    private String clauseDetail;
    private String attachment;
    private String remark;
    private String bankAccount;
    private Long deptId;
    private String deptName;
    private Long applicantId;
    private String applicantName;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
