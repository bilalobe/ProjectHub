package com.projecthub.base.submission.infrastructure.ratelimit;

import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SubmissionRateLimitService {
    private final Bucket studentRateLimit;
    private final Bucket instructorRateLimit;
    private final CacheManager cacheManager;
    private final ConcurrentHashMap<String, Bucket> userBuckets = new ConcurrentHashMap<>();

    public boolean tryConsume(Authentication auth) {
        String userId = auth.getName();
        Bucket bucket = getUserBucket(auth);
        return bucket.tryConsume(1);
    }

    public Duration getWaitTime(Authentication auth) {
        String userId = auth.getName();
        Bucket bucket = getUserBucket(auth);
        return bucket.getAvailableTokens() > 0 ? Duration.ZERO : 
               bucket.estimateAbilityToConsume(1);
    }

    private Bucket getUserBucket(Authentication auth) {
        String userId = auth.getName();
        return userBuckets.computeIfAbsent(userId, key -> 
            auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_INSTRUCTOR")) ?
            instructorRateLimit : studentRateLimit
        );
    }

    public void clearUserBucket(String userId) {
        userBuckets.remove(userId);
    }
}