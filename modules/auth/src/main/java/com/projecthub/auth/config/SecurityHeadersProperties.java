package com.projecthub.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.HashMap;

@Component
@ConfigurationProperties(prefix = "projecthub.security.headers")
public class SecurityHeadersProperties {
    private Map<String, String> headers = new HashMap<>();

    public SecurityHeadersProperties() {
        // Set default security headers
        headers.put("X-Content-Type-Options", "nosniff");
        headers.put("X-Frame-Options", "DENY");
        headers.put("X-XSS-Protection", "1; mode=block");
        headers.put("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        headers.put("Content-Security-Policy", "default-src 'self'; frame-ancestors 'none'");
        headers.put("Referrer-Policy", "strict-origin-when-cross-origin");
        headers.put("Permissions-Policy", "geolocation=(), camera=()");
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}