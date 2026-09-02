package com.tt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tt.entity.FieldVisibilityConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FieldVisibilityConfigMapper extends BaseMapper<FieldVisibilityConfig> {

    @Select("SELECT * FROM field_visibility_config WHERE stage = #{stage}")
    List<FieldVisibilityConfig> selectByStage(String stage);

    @Select("SELECT * FROM field_visibility_config ORDER BY stage, field_name")
    List<FieldVisibilityConfig> selectAll();
}
