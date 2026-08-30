package com.tt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("field_visibility_config")
public class FieldVisibilityConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String stage;
    private String fieldName;
    /** VISIBLE / MASKED / HIDDEN */
    private String visibility;
}
