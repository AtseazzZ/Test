package com.tt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_dept")
public class UserDept {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long deptId;
}
