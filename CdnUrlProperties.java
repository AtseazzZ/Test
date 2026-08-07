package com.gzhu.csnet.kclab.classics100common.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "c100.cdn")
public class CdnUrlProperties {
    private static final String DEFAULT_AVATAR_OBJECT_KEY = "classics100/default-avatar.png";
    private static final String FALLBACK_DEFAULT_AVATAR_URL = "/static/default-avatar.png";

    private String cdnUrl;
    private String defaultAvatarUrl;

    public String getDefaultAvatarUrl() {
        if (StringUtils.hasText(defaultAvatarUrl)) {
            return defaultAvatarUrl;
        }
        if (StringUtils.hasText(cdnUrl)) {
            return appendCdnObjectKey(cdnUrl, DEFAULT_AVATAR_OBJECT_KEY);
        }
        return FALLBACK_DEFAULT_AVATAR_URL;
    }

    private String appendCdnObjectKey(String baseUrl, String objectKey) {
        if (baseUrl.endsWith("/")) {
            return baseUrl + objectKey;
        }
        return baseUrl + "/" + objectKey;
    }
}
