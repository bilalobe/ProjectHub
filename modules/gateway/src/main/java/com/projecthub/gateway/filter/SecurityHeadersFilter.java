package com.projecthub.gateway.filter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.HashMap;

/**
 * Global filter that adds security-related HTTP headers to all responses.
 * 
 * <p>This filter enhances security by adding headers like Content-Security-Policy,
 * X-XSS-Protection, etc. to all responses from the gateway.</p>
 */
@Component
@ConfigurationProperties(prefix = "projecthub.gateway.security.cross-cutting.security-headers")
public class SecurityHeadersFilter implements GlobalFilter, Ordered {

    private final Map<String, String> headers = new HashMap<>();

    /**
     * Sets the security headers from configuration.
     * 
     * @param headers Map of header names to values
     */
    public void setHeaders(Map<String, String> headers) {
        if (headers != null) {
            this.headers.putAll(headers);
        }
    }

    /**
     * Adds default security headers if not specified in configuration.
     */
    private void addDefaultHeaders() {
        if (!headers.containsKey("X-Content-Type-Options")) {
            headers.put("X-Content-Type-Options", "nosniff");
        }
        if (!headers.containsKey("X-Frame-Options")) {
            headers.put("X-Frame-Options", "DENY");
        }
        if (!headers.containsKey("X-XSS-Protection")) {
            headers.put("X-XSS-Protection", "1; mode=block");
        }
        if (!headers.containsKey("Strict-Transport-Security")) {
            headers.put("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        }
        if (!headers.containsKey("Cache-Control")) {
            headers.put("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        }
        if (!headers.containsKey("Pragma")) {
            headers.put("Pragma", "no-cache");
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Add default headers if none configured
        if (headers.isEmpty()) {
            addDefaultHeaders();
        }
        
        // Add security headers to the response
        ServerHttpRequest request = exchange.getRequest();
        exchange.getResponse().getHeaders().forEach((name, values) -> {
            // Don't override existing headers from downstream services
            headers.entrySet().stream()
                .filter(entry -> !exchange.getResponse().getHeaders().containsKey(entry.getKey()))
                .forEach(entry -> exchange.getResponse().getHeaders().add(entry.getKey(), entry.getValue()));
        });

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // Execute late in the filter chain
        return Ordered.LOWEST_PRECEDENCE - 10;
    }
}