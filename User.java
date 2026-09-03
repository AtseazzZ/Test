package com.tt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String realName;
    private Long primaryDeptId;
    private LocalDateTime createTime;

    /** 非数据库字段：角色列表 */
    @TableField(exist = false)
    private List<Role> roles;

    /** 非数据库字段：主部门名称 */
    @TableField(exist = false)
    private String primaryDeptName;
}
