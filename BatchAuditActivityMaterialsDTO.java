package com.gzhu.csnet.kclab.classics100pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BatchAuditActivityMaterialsDTO {

    private Long activityId;
    private List<Long> studentIds;
    private Integer auditStatus;
    private String auditComment;
    private Long auditorId;
    private LocalDateTime auditTime;
    private LocalDateTime updateTime;

}
