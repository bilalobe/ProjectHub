package com.projecthub.base.student.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class StudentMetrics {

    private final MeterRegistry registry;
    private final Counter studentCreationCounter;
    private final Counter studentUpdateCounter;
    private final Counter studentDeletionCounter;

    public StudentMetrics(MeterRegistry registry) {
        this.registry = registry;
        this.studentCreationCounter = registry.counter("student.operations", "type", "creation");
        this.studentUpdateCounter = registry.counter("student.operations", "type", "update");
        this.studentDeletionCounter = registry.counter("student.operations", "type", "deletion");
    }

    @Around("execution(* com.projecthub.base.student.application.service.StudentCommandService.createStudent(..))")
    public Object measureCreateStudent(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start(registry);
        try {
            Object result = joinPoint.proceed();
            studentCreationCounter.increment();
            return result;
        } finally {
            sample.stop(registry.timer("student.operation.timing",
                "operation", "create",
                "method", joinPoint.getSignature().getName()));
        }
    }

    @Around("execution(* com.projecthub.base.student.application.service.StudentCommandService.updateStudent(..))")
    public Object measureUpdateStudent(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start(registry);
        try {
            Object result = joinPoint.proceed();
            studentUpdateCounter.increment();
            return result;
        } finally {
            sample.stop(registry.timer("student.operation.timing",
                "operation", "update",
                "method", joinPoint.getSignature().getName()));
        }
    }

    @Around("execution(* com.projecthub.base.student.application.service.StudentCommandService.deleteStudent(..))")
    public Object measureDeleteStudent(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start(registry);
        try {
            Object result = joinPoint.proceed();
            studentDeletionCounter.increment();
            return result;
        } finally {
            sample.stop(registry.timer("student.operation.timing",
                "operation", "delete",
                "method", joinPoint.getSignature().getName()));
        }
    }

    @Around("execution(* com.projecthub.base.student.application.service.StudentQueryService.*(..))")
    public Object measureQueryOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start(registry);
        try {
            return joinPoint.proceed();
        } finally {
            sample.stop(registry.timer("student.query.timing",
                "method", joinPoint.getSignature().getName()));
        }
    }
}
