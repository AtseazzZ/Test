package com.tt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tt.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /** 查询多个角色的所有权限（含角色继承） */
    @Select("<script>" +
            "SELECT DISTINCT p.* FROM permission p " +
            "JOIN role_permission rp ON rp.permission_id = p.id " +
            "WHERE rp.role_id IN " +
            "<foreach collection='roleIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    List<Permission> selectByRoleIds(List<Long> roleIds);
}
