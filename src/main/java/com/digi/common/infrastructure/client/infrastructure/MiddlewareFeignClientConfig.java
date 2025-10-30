package com.digi.common.infrastructure.client.infrastructure;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration for Middleware Feign Client
 * Configures timeouts, logging, and error handling
 */
@Configuration
public class MiddlewareFeignClientConfig {

    @Value("${middleware.connect-timeout:10000}")
    private int connectTimeout;

    @Value("${middleware.read-timeout:30000}")
    private int readTimeout;

    /**
     * Configure request options (timeouts)
     */
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
            connectTimeout, TimeUnit.MILLISECONDS,
            readTimeout, TimeUnit.MILLISECONDS,
            true
        );
    }

    /**
     * Configure Feign logging level
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * Custom error decoder for Feign client
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new MiddlewareFeignErrorDecoder();
    }
}