package com.projecthub.base.project.infrastructure.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ProjectOperationAspect {

    private final MeterRegistry registry;

    public ProjectOperationAspect(MeterRegistry registry) {
        this.registry = registry;
    }

    @Around("execution(* com.projecthub.base.project.application.service.*.*(..))")
    public Object logOperation(final ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start(registry);
        try {
            return joinPoint.proceed();
        } finally {
            sample.stop(registry.timer("project.operation.timing",
                "class", joinPoint.getTarget().getClass().getSimpleName(),
                "method", joinPoint.getSignature().getName()));
        }
    }
}
