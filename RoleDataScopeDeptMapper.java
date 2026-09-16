package com.tt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tt.entity.RoleDataScopeDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleDataScopeDeptMapper extends BaseMapper<RoleDataScopeDept> {

    @Select("SELECT dept_id FROM role_data_scope_dept WHERE scope_id = #{scopeId}")
    List<Long> selectDeptIdsByScopeId(Long scopeId);
}
