package com.gzhu.csnet.kclab.classics100pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaiduMapGeoEncodeResultDTO {
    private BaiduMapGeoLocationDTO location;
    private Integer precise;
    private Integer confidence;
    private Integer comprehension;
    private String level;
    private String analys_level;
}
