package com.projecthub.infrastructure.aspect

import com.projecthub.infrastructure.config.MilestoneMetrics
import com.projecthub.infrastructure.config.MilestoneResilience
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger

@Aspect
@Component
class MilestoneMonitoringAspect(
    private val metrics: MilestoneMetrics,
    private val resilience: MilestoneResilience
) {
    private val concurrentOperations = AtomicInteger(0)

    @Around("@within(com.projecthub.milestone.domain.MilestoneOperation)")
    fun monitorOperation(joinPoint: ProceedingJoinPoint): Any? {
        val operationName = joinPoint.signature.name
        val currentConcurrent = concurrentOperations.incrementAndGet()

        metrics.recordConcurrentOperations(operationName, currentConcurrent)

        val startTime = System.currentTimeMillis()
        try {
            val result = resilience.getCircuitBreaker(operationName).executeSupplier {
                resilience.getRetry(operationName).executeSupplier {
                    joinPoint.proceed()
                }
            }

            metrics.incrementOperationCount(operationName, "success")
            return result
        } catch (e: Exception) {
            metrics.incrementOperationCount(operationName, "failure")
            throw e
        } finally {
            val duration = System.currentTimeMillis() - startTime
            metrics.recordOperationTime(operationName, duration)
            concurrentOperations.decrementAndGet()
        }
    }
}
