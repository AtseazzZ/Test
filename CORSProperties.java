package com.gzhu.csnet.kclab.classics100common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "c100.cors")
@Data
public class CORSProperties {

    private List<String> domain;

}
