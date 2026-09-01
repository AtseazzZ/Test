package com.tt.vo;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ContractCreateRequest {
    private String contractNo;
    @NotBlank(message = "合同名称不能为空")
    private String name;
    @NotBlank(message = "客户不能为空")
    private String customer;
    private BigDecimal amount;
    private String clauseDetail;
    private String attachment;
    private String remark;
    private String bankAccount;
    private Long deptId;
}
