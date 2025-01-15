package com.projecthub.base.submission.infrastructure.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SubmissionMetrics {

    private final MeterRegistry registry;

    public SubmissionMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    @Around("execution(* com.projecthub.base.submission..*.*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start(registry);
        try {
            return joinPoint.proceed();
        } finally {
            sample.stop(registry.timer("submission.execution",
                "method", joinPoint.getSignature().getName()));
        }
    }
}
