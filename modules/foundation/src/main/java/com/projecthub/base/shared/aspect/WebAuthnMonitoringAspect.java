package com.projecthub.base.shared.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class WebAuthnMonitoringAspect {
    private static final Logger logger = LoggerFactory.getLogger(WebAuthnMonitoringAspect.class);

    @Pointcut("execution(* com.projecthub.base.auth.webauthn..*(..))")
    public void webAuthnOperations() {
    }

    @Around("webAuthnOperations()")
    public Object monitorWebAuthn(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String operation = joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            logger.info("WebAuthn operation: {} completed in {}ms", operation, duration);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("WebAuthn operation: {} failed after {}ms: {}",
                operation, duration, e.getMessage(), e);
            throw e;
        }
    }
}
