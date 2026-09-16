package com.tt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tt.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /** 查询用户的所有角色 */
    @Select("SELECT r.* FROM role r JOIN user_role ur ON ur.role_id = r.id WHERE ur.user_id = #{userId}")
    List<Role> selectRolesByUserId(Long userId);

    /** 递归查询角色及其所有父角色ID（角色层级继承） */
    @Select("WITH RECURSIVE role_hierarchy AS (" +
            "  SELECT id, parent_role_id FROM role WHERE id = #{roleId} " +
            "  UNION ALL " +
            "  SELECT r.id, r.parent_role_id FROM role r INNER JOIN role_hierarchy rh ON r.id = rh.parent_role_id " +
            ") SELECT id FROM role_hierarchy")
    List<Long> selectRoleHierarchy(Long roleId);
}
