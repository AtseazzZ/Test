package com.gzhu.csnet.kclab.classics100common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "c100.wechat.message")
public class WechatMessageProperties {
    private String token;
    private String encodingAesKey;
    private String appid;
}