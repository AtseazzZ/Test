package com.tt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tt.entity.Contract;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ContractMapper extends BaseMapper<Contract> {

    /**
     * 带数据范围条件的分页查询（参数化，防 SQL 注入）
     * #{scopeCondition} 由 DataScopeResolver 生成，使用 #{} 参数化
     */
    @Select("<script>" +
            "SELECT c.* FROM contract c " +
            "WHERE 1=1 " +
            "<if test='scopeCondition != null and scopeCondition != \"\"'>" +
            "  AND (${scopeCondition}) " +
            "</if>" +
            "<if test='status != null and status != \"\"'>" +
            "  AND c.status = #{status} " +
            "</if>" +
            "ORDER BY c.create_time DESC" +
            "</script>")
    IPage<Contract> selectPageWithScope(IPage<Contract> page,
                                        @Param("scopeCondition") String scopeCondition,
                                        @Param("status") String status);

    /** 按数据范围查询单个合同（用于详情越权防护：查不到返回 null → 404） */
    @Select("SELECT c.* FROM contract c WHERE c.id = #{id} AND (${scopeCondition})")
    Contract selectByIdWithScope(@Param("id") Long id,
                                 @Param("scopeCondition") String scopeCondition);
}
