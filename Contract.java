package com.tt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("contract")
public class Contract {
    @TableId(type = IdType.AUTO)
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
    private Long applicantId;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
