package com.gzhu.csnet.kclab.classics100pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDailyStatsDTO {

    private LocalDate date;
    private Integer activityCount;
    private Integer submitCount;
    private Integer approvedCount;

}
