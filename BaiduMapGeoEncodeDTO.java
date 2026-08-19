package com.gzhu.csnet.kclab.classics100pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaiduMapGeoEncodeDTO {

    private Integer status;
    private BaiduMapGeoEncodeResultDTO result;

}
