package com.projecthub.infrastructure.resilience.milestone

import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import io.github.resilience4j.retry.RetryRegistry
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.dao.TransientDataAccessException
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.TimeoutException

@Aspect
@Component
class MilestoneRetryAspect {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val retryRegistry: RetryRegistry = RetryRegistry.of(
        RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofMillis(500))
            .retryExceptions(
                TransientDataAccessException::class.java,
                TimeoutException::class.java
            )
            .build()
    )

    @Pointcut("execution(* com.projecthub.infrastructure.persistence.milestone.JpaMilestoneRepository.*(..))")
    fun repositoryOperations() {
    }

    @Around("repositoryOperations()")
    fun retryOperation(joinPoint: ProceedingJoinPoint): Any {
        val methodName = joinPoint.signature.name
        val retry = retryRegistry.retry("milestone-repository-$methodName")

        return Retry.decorateSupplier(retry) {
            try {
                joinPoint.proceed()
            } catch (e: Exception) {
                logger.warn("Operation {} failed, will retry if eligible: {}", methodName, e.message)
                throw e
            }
        }.get()
    }

    @Pointcut("execution(* com.projecthub.infrastructure.messaging.milestone.MilestoneEventPublisher.*(..))")
    fun eventPublishingOperations() {
    }

    @Around("eventPublishingOperations()")
    fun retryEventPublishing(joinPoint: ProceedingJoinPoint): Any {
        val methodName = joinPoint.signature.name
        val retry = retryRegistry.retry("milestone-events-$methodName")

        return Retry.decorateSupplier(retry) {
            try {
                joinPoint.proceed()
            } catch (e: Exception) {
                logger.warn("Event publishing {} failed, will retry if eligible: {}", methodName, e.message)
                throw e
            }
        }.get()
    }

    @Pointcut("execution(* com.projecthub.infrastructure.integration.milestone.*.*(..))")
    fun integrationOperations() {
    }

    @Around("integrationOperations()")
    fun retryIntegration(joinPoint: ProceedingJoinPoint): Any {
        val methodName = joinPoint.signature.name
        val retry = retryRegistry.retry("milestone-integration-$methodName")

        return Retry.decorateSupplier(retry) {
            try {
                joinPoint.proceed()
            } catch (e: Exception) {
                logger.warn("Integration operation {} failed, will retry if eligible: {}", methodName, e.message)
                throw e
            }
        }.get()
    }
}
