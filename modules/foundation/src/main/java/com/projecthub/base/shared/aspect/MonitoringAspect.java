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
public class MonitoringAspect {
    private static final Logger logger = LoggerFactory.getLogger(MonitoringAspect.class);

    @Pointcut("within(com.projecthub.base.controller..*) || within(com.projecthub.base.service..*)")
    public void coreComponentsPointcut() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
        "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
        "@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void restEndpointsPointcut() {
    }

    @Around("coreComponentsPointcut() || restEndpointsPointcut()")
    public Object monitorPerformance(final ProceedingJoinPoint joinPoint) throws Throwable {
        final long startTime = System.currentTimeMillis();
        final String methodName = joinPoint.getSignature().toShortString();

        try {
            final Object result = joinPoint.proceed();
            final long executionTime = System.currentTimeMillis() - startTime;
            this.logMethodExecution(methodName, executionTime, null);
            return this.filterSensitiveData(result);
        } catch (final Exception e) {
            final long executionTime = System.currentTimeMillis() - startTime;
            this.logMethodExecution(methodName, executionTime, e);
            throw e;
        }
    }

    private void logMethodExecution(final String methodName, final long executionTime, final Exception error) {
        final String logMessage = String.format("Method: %s | Duration: %dms | Status: %s",
            methodName, executionTime, null == error ? "SUCCESS" : "ERROR");
        if (null == error) {
            MonitoringAspect.logger.info(logMessage);
        } else {
            MonitoringAspect.logger.error("{} | Error: {}", logMessage, error.getMessage());
        }
    }

    private Object filterSensitiveData(final Object result) {
        // Implement security filtering logic here
        // For example, remove passwords, tokens, or other sensitive data
        return result;
    }
}
