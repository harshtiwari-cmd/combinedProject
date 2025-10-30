package com.digi.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for dkn-middleware-service
 */
@Data
@Component
@ConfigurationProperties(prefix = "middleware")
public class MiddlewareProperties {
    
    private String baseUrl;
    private String apiPath = "/api/v1/bank-middleware";
    private int connectTimeout = 10000;
    private int readTimeout = 30000;
    
    public String getFullUrl() {
        return baseUrl + apiPath;
    }
}