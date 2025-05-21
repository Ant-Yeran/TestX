package com.it.testx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Google Cloud 基本配置
 */
@Configuration
@ConfigurationProperties(prefix = "gcp")
@Data
public class CloudConfig {
    /**
     * 项目 ID
     */
    private String projectId;

    /**
     * 密钥路径
     */
    private String credentialsPath;
}