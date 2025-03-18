package com.projecthub.infrastructure.metrics.milestone

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
class MilestoneMetricsAspect(
    private val meterRegistry: MeterRegistry
) {

    @Pointcut("execution(* com.projecthub.application.milestone.service.MilestoneManagementService.*(..))")
    fun milestoneOperations() {
    }

    @Around("milestoneOperations()")
    fun recordMetrics(joinPoint: ProceedingJoinPoint): Any {
        val methodName = joinPoint.signature.name
        val startTime = System.nanoTime()

        return try {
            val result = joinPoint.proceed()
            recordSuccess(methodName, startTime)
            result
        } catch (e: Exception) {
            recordError(methodName, startTime, e)
            throw e
        }
    }

    private fun recordSuccess(methodName: String, startTime: Long) {
        Timer.builder("milestone.operation.latency")
            .tag("operation", methodName)
            .tag("status", "success")
            .register(meterRegistry)
            .record(System.nanoTime() - startTime, TimeUnit.NANOSECONDS)

        meterRegistry.counter(
            "milestone.operation.total",
            "operation", methodName,
            "status", "success"
        ).increment()
    }

    private fun recordError(methodName: String, startTime: Long, error: Exception) {
        Timer.builder("milestone.operation.latency")
            .tag("operation", methodName)
            .tag("status", "error")
            .tag("error_type", error.javaClass.simpleName)
            .register(meterRegistry)
            .record(System.nanoTime() - startTime, TimeUnit.NANOSECONDS)

        meterRegistry.counter(
            "milestone.operation.total",
            "operation", methodName,
            "status", "error",
            "error_type", error.javaClass.simpleName
        ).increment()
    }

    // Additional metrics for validation operations
    @Pointcut("execution(* com.projecthub.domain.milestone.validation.MilestoneValidationService.*(..))")
    fun validationOperations() {
    }

    @Around("validationOperations()")
    fun recordValidationMetrics(joinPoint: ProceedingJoinPoint): Any {
        val methodName = joinPoint.signature.name
        val startTime = System.nanoTime()

        return try {
            val result = joinPoint.proceed()

            meterRegistry.timer(
                "milestone.validation.duration",
                "validation", methodName,
                "status", "success"
            ).record(System.nanoTime() - startTime, TimeUnit.NANOSECONDS)

            meterRegistry.counter(
                "milestone.validation.total",
                "validation", methodName,
                "status", "success"
            ).increment()

            result
        } catch (e: Exception) {
            meterRegistry.timer(
                "milestone.validation.duration",
                "validation", methodName,
                "status", "error",
                "error_type", e.javaClass.simpleName
            ).record(System.nanoTime() - startTime, TimeUnit.NANOSECONDS)

            meterRegistry.counter(
                "milestone.validation.total",
                "validation", methodName,
                "status", "error",
                "error_type", e.javaClass.simpleName
            ).increment()

            throw e
        }
    }

    // Metric collecting for milestone state transitions
    @Pointcut("execution(* com.projecthub.domain.milestone.MilestoneState.*(..))")
    fun stateTransitions() {
    }

    @Around("stateTransitions()")
    fun recordStateTransition(joinPoint: ProceedingJoinPoint): Any {
        val fromState = joinPoint.target.toString()
        val result = joinPoint.proceed()
        val toState = result.toString()

        meterRegistry.counter(
            "milestone.state.transitions",
            "from", fromState,
            "to", toState
        ).increment()

        return result
    }
}
