package com.projecthub.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for rate limiting in the gateway.
 * Allows for endpoint-specific rate limit configurations.
 */
@Configuration
@ConfigurationProperties(prefix = "projecthub.rate-limiting")
@Data
public class RateLimitingConfig {
    
    /**
     * Default limit for requests per window if not specified for an endpoint.
     */
    private int defaultLimit = 100;
    
    /**
     * Default time window in seconds for rate limiting.
     */
    private int defaultWindowInSeconds = 60;
    
    /**
     * Map of endpoint patterns to their specific rate limit configurations.
     */
    private Map<String, EndpointLimit> endpoints = new HashMap<>();
    
    /**
     * Configuration for a specific endpoint rate limit.
     */
    @Data
    public static class EndpointLimit {
        /**
         * Maximum number of requests allowed in the time window.
         */
        private int limit;
        
        /**
         * Time window in seconds for this endpoint's rate limiting.
         */
        private int windowInSeconds;
    }
    
    /**
     * Get rate limit for a specific path.
     * 
     * @param path API path to check
     * @return The rate limit configuration, or default if not found
     */
    public int getLimitForPath(String path) {
        for (Map.Entry<String, EndpointLimit> entry : endpoints.entrySet()) {
            if (path.matches(entry.getKey())) {
                return entry.getValue().getLimit();
            }
        }
        return defaultLimit;
    }
    
    /**
     * Get time window in seconds for a specific path.
     * 
     * @param path API path to check
     * @return The time window in seconds, or default if not found
     */
    public int getWindowForPath(String path) {
        for (Map.Entry<String, EndpointLimit> entry : endpoints.entrySet()) {
            if (path.matches(entry.getKey())) {
                return entry.getValue().getWindowInSeconds();
            }
        }
        return defaultWindowInSeconds;
    }
}