package com.it.testx.config.gcp;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyManagementServiceSettings;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PreDestroy;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Google Cloud KMS 客户端配置类
 */
@Configuration
@Data
public class KmsConfig {
    /**
     * Google Cloud 基本配置
     */
    private final CloudConfig cloudConfig;

    /**
     * Google Cloud KMS 客户端
     */
    private KeyManagementServiceClient kmsClient;

    public KmsConfig(CloudConfig cloudConfig) {
        this.cloudConfig = cloudConfig;
    }

    @Bean
    @Primary
    public KeyManagementServiceClient kmsClient() throws IOException {
        GoogleCredentials credentials;
        try (InputStream credentialsStream = new FileInputStream(cloudConfig.getCredentialsPath())) {
            credentials = GoogleCredentials.fromStream(credentialsStream);
        }

        // 配置 KMS 客户端选项
        KeyManagementServiceSettings settings = KeyManagementServiceSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        this.kmsClient = KeyManagementServiceClient.create(settings);
        return kmsClient;
    }

    @PreDestroy
    public void close() {
        if (kmsClient != null) {
            try {
                kmsClient.close();
            } catch (Exception e) {
                // 记录日志但不要抛出异常
                System.err.println("Error closing KMS client: " + e.getMessage());
            }
        }
    }
}