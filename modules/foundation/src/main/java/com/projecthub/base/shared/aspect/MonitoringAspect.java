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
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            logMethodExecution(methodName, executionTime, null);
            return filterSensitiveData(result);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logMethodExecution(methodName, executionTime, e);
            throw e;
        }
    }

    private void logMethodExecution(String methodName, long executionTime, Exception error) {
        String logMessage = String.format("Method: %s | Duration: %dms | Status: %s",
            methodName, executionTime, error == null ? "SUCCESS" : "ERROR");
        if (error == null) {
            logger.info(logMessage);
        } else {
            logger.error("{} | Error: {}", logMessage, error.getMessage());
        }
    }

    private Object filterSensitiveData(Object result) {
        // Implement security filtering logic here
        // For example, remove passwords, tokens, or other sensitive data
        return result;
    }
}
