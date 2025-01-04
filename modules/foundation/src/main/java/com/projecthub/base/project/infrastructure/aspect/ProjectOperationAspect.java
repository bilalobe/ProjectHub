package com.projecthub.base.project.infrastructure.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ProjectOperationAspect {
    @Around("execution(* com.projecthub.base.project.application.service.*.*(..))")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        // Logging, metrics, etc.
        return joinPoint.proceed();
    }
}
