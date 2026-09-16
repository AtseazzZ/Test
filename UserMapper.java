package com.tt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tt.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /** 查询用户的主部门名称 */
    @Select("SELECT d.name FROM dept d JOIN sys_user u ON u.primary_dept_id = d.id WHERE u.id = #{userId}")
    String selectDeptNameByUserId(Long userId);

    /** 查询用户所有部门ID（主部门+兼任部门） */
    @Select("SELECT primary_dept_id FROM sys_user WHERE id = #{userId} " +
            "UNION SELECT dept_id FROM user_dept WHERE user_id = #{userId}")
    List<Long> selectUserDeptIds(Long userId);
}
