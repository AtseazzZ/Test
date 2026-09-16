package com.tt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tt.entity.ApprovalFlowConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ApprovalFlowConfigMapper extends BaseMapper<ApprovalFlowConfig> {

    @Select("SELECT * FROM approval_flow_config WHERE from_status = #{fromStatus} AND action = #{action}")
    ApprovalFlowConfig selectByFromStatusAndAction(String fromStatus, String action);
}
