package com.licensed.remitprod.web.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "security")
@Data
public class SecurityProperties {

    private List<String> whitelist = new ArrayList<>();

    private Cors cors = new Cors();

    @Data
    public static class Cors {
        private boolean enabled = true;
        private boolean allowCredentials;
        private String allowedOrigins;
        private String allowedHeaders;
        private String allowedMethods;
        private String pathPattern;
    }
}