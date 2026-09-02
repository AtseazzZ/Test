package com.tt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tt.entity.ApprovalRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ApprovalRecordMapper extends BaseMapper<ApprovalRecord> {

    @Select("SELECT * FROM approval_record WHERE contract_id = #{contractId} ORDER BY create_time ASC")
    List<ApprovalRecord> selectByContractId(Long contractId);
}
