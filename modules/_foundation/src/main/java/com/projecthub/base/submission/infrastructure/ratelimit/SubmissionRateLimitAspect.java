package com.projecthub.base.submission.infrastructure.ratelimit;

import com.projecthub.base.shared.exception.RateLimitExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SubmissionRateLimitAspect {
    private final SubmissionRateLimitService rateLimitService;

    @Around("execution(* com.projecthub.base.submission.api.controller.*.*(..))")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!rateLimitService.tryConsume(auth)) {
            var waitTime = rateLimitService.getWaitTime(auth);
            log.warn("Rate limit exceeded for user {}, must wait {}", auth.getName(), waitTime);
            throw new RateLimitExceededException(
                String.format("Rate limit exceeded. Please try again in %s seconds",
                        Long.valueOf(waitTime.getSeconds()))
            );
        }

        return joinPoint.proceed();
    }
}
