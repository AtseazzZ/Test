package com.tt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.List;

@Data
@TableName("role_data_scope")
public class RoleDataScope {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long roleId;
    /** SELF / DEPT / CUSTOM / ALL */
    private String scopeType;

    @TableField(exist = false)
    private List<Long> customDeptIds;
}
