package com.projecthub.auth.filter;

import com.projecthub.auth.config.SecurityHeadersProperties;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * WebFilter that adds security headers to all responses.
 * This filter can be used by both gateway and service modules.
 */
@Component
public class SecurityHeadersFilter implements WebFilter, Ordered {

    private final SecurityHeadersProperties headersProperties;

    public SecurityHeadersFilter(SecurityHeadersProperties headersProperties) {
        this.headersProperties = headersProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        headersProperties.getHeaders().forEach((name, value) -> 
            exchange.getResponse().getHeaders().add(name, value));
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // Execute early in the filter chain
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}