package com.tt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tt.entity.RoleDataScope;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleDataScopeMapper extends BaseMapper<RoleDataScope> {

    @Select("SELECT * FROM role_data_scope WHERE role_id = #{roleId}")
    RoleDataScope selectByRoleId(Long roleId);

    @Select("<script>" +
            "SELECT * FROM role_data_scope WHERE role_id IN " +
            "<foreach collection='roleIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    List<RoleDataScope> selectByRoleIds(List<Long> roleIds);
}
