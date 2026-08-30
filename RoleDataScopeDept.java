package com.tt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("role_data_scope_dept")
public class RoleDataScopeDept {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long scopeId;
    private Long deptId;
}
