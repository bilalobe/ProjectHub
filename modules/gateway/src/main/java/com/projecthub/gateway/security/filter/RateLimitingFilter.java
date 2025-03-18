package com.projecthub.gateway.security.filter;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Gateway filter that implements rate limiting based on IP address.
 * Prevents excessive requests from a single source.
 */
@Component
@Order(0) // Execute before authentication filter
public class RateLimitingFilter implements WebFilter {

    // Simple in-memory rate limiting (would use Redis or similar in production)
    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> resetTimes = new ConcurrentHashMap<>();
    
    // Rate limiting configuration
    private static final int MAX_REQUESTS = 100; // Maximum requests per window
    private static final Duration WINDOW = Duration.ofMinutes(1); // Time window for rate limiting

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String clientIp = getClientIp(exchange);
        long currentTime = System.currentTimeMillis();
        
        // Reset counter if time window has expired
        resetTimes.compute(clientIp, (key, resetTime) -> {
            if (resetTime == null || currentTime > resetTime) {
                requestCounts.put(key, new AtomicInteger(0));
                return currentTime + WINDOW.toMillis();
            }
            return resetTime;
        });
        
        // Increment and check counter
        AtomicInteger counter = requestCounts.getOrDefault(clientIp, new AtomicInteger(0));
        int count = counter.incrementAndGet();
        requestCounts.put(clientIp, counter);
        
        if (count > MAX_REQUESTS) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().add("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS));
            exchange.getResponse().getHeaders().add("X-RateLimit-Reset", String.valueOf(resetTimes.get(clientIp)));
            return exchange.getResponse().setComplete();
        }
        
        return chain.filter(exchange);
    }
    
    private String getClientIp(ServerWebExchange exchange) {
        // Try to get the real IP if behind a proxy
        String ip = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = exchange.getRequest().getRemoteAddress().getHostString();
        }
        // If we get a comma-separated list of IPs, take the first one
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}