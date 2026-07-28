package com.gzhu.csnet.kclab.classics100pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityMaterialsQueryDTO {

    private Long activityId;
    private String keyword;
    private String studentNumber;
    private String studentName;
    private String className;
    private Integer auditStatus;

}
