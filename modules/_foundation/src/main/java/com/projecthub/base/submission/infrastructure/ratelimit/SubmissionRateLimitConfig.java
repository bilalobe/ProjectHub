package com.projecthub.base.submission.infrastructure.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class SubmissionRateLimitConfig {

    public SubmissionRateLimitConfig() {
    }

    @Bean
    public SubmissionRateLimitService submissionRateLimitService(CacheManager cacheManager) {
        return new SubmissionRateLimitService(
            createStudentRateLimit(),
            createInstructorRateLimit(),
            cacheManager
        );
    }

    private Bucket createStudentRateLimit() {
        return Bucket.builder()
            .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1L)))) // 10 per minute
            .addLimit(Bandwidth.classic(100, Refill.intervally(100, Duration.ofHours(1L)))) // 100 per hour
            .build();
    }

    private Bucket createInstructorRateLimit() {
        return Bucket.builder()
            .addLimit(Bandwidth.classic(50, Refill.intervally(50, Duration.ofMinutes(1L)))) // 50 per minute
            .addLimit(Bandwidth.classic(1000, Refill.intervally(1000, Duration.ofHours(1L)))) // 1000 per hour
            .build();
    }
}
