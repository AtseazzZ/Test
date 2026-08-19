package com.gzhu.csnet.kclab.classics100pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityRosterExportSnapshotDTO {

    private Long activityId;
    private List<Long> materialIds;
    private String fileName;
}
