package com.gzhu.csnet.kclab.classics100common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "c100.baidu.map")
@Data
public class BaiduMapProperties {

    private String ak;

}
