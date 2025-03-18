package com.projecthub.infrastructure.monitoring.milestone

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Aspect
@Component
class MilestonePerformanceAspect(
    private val meterRegistry: MeterRegistry
) {

    @Pointcut("execution(* com.projecthub.application.milestone.service.MilestoneManagementService.*(..))")
    fun milestoneOperations() {
    }

    @Around("milestoneOperations()")
    fun measureOperationTime(joinPoint: ProceedingJoinPoint): Any {
        val start = System.nanoTime()
        val methodName = joinPoint.signature.name

        try {
            val result = joinPoint.proceed()

            Timer.builder("milestone.operation.duration")
                .tag("operation", methodName)
                .tag("status", "success")
                .register(meterRegistry)
                .record(System.nanoTime() - start, TimeUnit.NANOSECONDS)

            // Track success count
            meterRegistry.counter(
                "milestone.operation.count",
                "operation", methodName,
                "status", "success"
            ).increment()

            return result

        } catch (e: Exception) {
            // Track failure metrics
            Timer.builder("milestone.operation.duration")
                .tag("operation", methodName)
                .tag("status", "error")
                .tag("error_type", e.javaClass.simpleName)
                .register(meterRegistry)
                .record(System.nanoTime() - start, TimeUnit.NANOSECONDS)

            meterRegistry.counter(
                "milestone.operation.count",
                "operation", methodName,
                "status", "error",
                "error_type", e.javaClass.simpleName
            ).increment()

            throw e
        }
    }

    @Pointcut("execution(* com.projecthub.domain.milestone.validation.MilestoneValidationService.*(..))")
    fun validationOperations() {
    }

    @Around("validationOperations()")
    fun measureValidationTime(joinPoint: ProceedingJoinPoint): Any {
        val start = System.nanoTime()
        val methodName = joinPoint.signature.name

        try {
            val result = joinPoint.proceed()

            Timer.builder("milestone.validation.duration")
                .tag("validation", methodName)
                .tag("status", "success")
                .register(meterRegistry)
                .record(System.nanoTime() - start, TimeUnit.NANOSECONDS)

            return result

        } catch (e: Exception) {
            Timer.builder("milestone.validation.duration")
                .tag("validation", methodName)
                .tag("status", "error")
                .tag("error_type", e.javaClass.simpleName)
                .register(meterRegistry)
                .record(System.nanoTime() - start, TimeUnit.NANOSECONDS)

            throw e
        }
    }
}
