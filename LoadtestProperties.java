package com.gzhu.csnet.kclab.classics100common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "c100.loadtest")
public class LoadtestProperties {

    private boolean enabled = false;
    private String accessKey;
    private int defaultTtlMinutes = 30;
    private int maxTtlMinutes = 120;
    private int maxBatchSize = 500;
    private int issueRateLimitPerMinute = 5;
    private String studentNumberPrefix = "LT";

}
