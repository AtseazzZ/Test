package com.gzhu.csnet.kclab.classics100pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivitySummaryDTO {

    private Integer activityCount;
    private Integer submitCount;
    private Integer approvedCount;
    private Integer rejectedCount;

}
