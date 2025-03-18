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

    public WebAuthnMonitoringAspect() {
    }

    @Pointcut("execution(* com.projecthub.base.auth.webauthn..*(..))")
    public void webAuthnOperations() {
    }

    @Around("webAuthnOperations()")
    public static Object monitorWebAuthn(final ProceedingJoinPoint joinPoint) throws Throwable, Exception {
        final long startTime = System.currentTimeMillis();
        final String operation = joinPoint.getSignature().toShortString();

        try {
            final Object result = joinPoint.proceed();
            final long duration = System.currentTimeMillis() - startTime;
            WebAuthnMonitoringAspect.logger.info("WebAuthn operation: {} completed in {}ms", operation, Long.valueOf(duration));
            return result;
        } catch (final Exception e) {
            final long duration = System.currentTimeMillis() - startTime;
            WebAuthnMonitoringAspect.logger.error("WebAuthn operation: {} failed after {}ms: {}",
                operation, Long.valueOf(duration), e.getMessage(), e);
            throw e;
        }
    }
}
