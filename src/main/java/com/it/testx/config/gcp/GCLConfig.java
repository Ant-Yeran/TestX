package com.it.testx.config.gcp;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.LoggingOptions;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PreDestroy;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Google Cloud Logging 客户端配置类
 */
@Configuration
@Data
public class GCLConfig {
    /**
     * Google Cloud 基本配置
     */
    private final CloudConfig cloudConfig;

    /**
     * Google Cloud Logging 客户端
     */
    private Logging gclClient;

    public GCLConfig(CloudConfig cloudConfig) {
        this.cloudConfig = cloudConfig;
    }

    @Bean
    @Primary
    public Logging gclClient() throws IOException {

        GoogleCredentials credentials;
        try (InputStream credentialsStream = new FileInputStream(cloudConfig.getCredentialsPath())) {
            credentials = GoogleCredentials.fromStream(credentialsStream);
        }

        // 配置 Logging 选项，明确指定项目
        this.gclClient = LoggingOptions.newBuilder()
                .setProjectId(cloudConfig.getProjectId())
                .setCredentials(credentials)
                .build()
                .getService();

        return gclClient;
    }

    @PreDestroy
    public void close() {
        if (gclClient != null) {
            try {
                gclClient.close();
            } catch (Exception e) {
                // TODO 记录日志但不要抛出异常
            }
        }
    }
}
