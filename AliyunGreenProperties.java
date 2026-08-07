package com.gzhu.csnet.kclab.classics100common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "c100.green")
public class AliyunGreenProperties {
    private boolean enabled;
    private String accessKeyId;
    private String accessKeySecret;
    private String endpoint;
    private String regionId;
    private String imageService;
    private String textService;
    private Integer connectTimeoutMillis;
    private Integer readTimeoutMillis;
    private Long imageUrlExpireSeconds;
}
