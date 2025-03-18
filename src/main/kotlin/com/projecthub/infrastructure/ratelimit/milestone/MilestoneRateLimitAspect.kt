package com.projecthub.infrastructure.ratelimit.milestone

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Bucket4j
import io.github.bucket4j.Refill
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Aspect
@Component
class MilestoneRateLimitAspect {

    private val buckets = ConcurrentHashMap<String, Bucket>()

    // Define rate limits
    private val defaultLimit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)))
    private val writeLimit = Bandwidth.classic(20, Refill.intervally(20, Duration.ofMinutes(1)))

    @Pointcut("execution(* com.projecthub.interfaces.web.milestone.MilestoneController.*(..))")
    fun milestoneEndpoints() {
    }

    @Around("milestoneEndpoints()")
    fun checkRateLimit(joinPoint: ProceedingJoinPoint): Any {
        val methodName = joinPoint.signature.name
        val bucket = getBucket(methodName)

        if (!bucket.tryConsume(1)) {
            throw ResponseStatusException(
                HttpStatus.TOO_MANY_REQUESTS,
                "Rate limit exceeded for milestone operations. Please try again later."
            )
        }

        return joinPoint.proceed()
    }

    private fun getBucket(methodName: String): Bucket {
        return buckets.computeIfAbsent(methodName) { key ->
            when {
                isWriteOperation(key) -> Bucket4j.builder()
                    .addLimit(writeLimit)
                    .build()

                else -> Bucket4j.builder()
                    .addLimit(defaultLimit)
                    .build()
            }
        }
    }

    private fun isWriteOperation(methodName: String): Boolean {
        return methodName.startsWith("create") ||
            methodName.startsWith("update") ||
            methodName.startsWith("delete") ||
            methodName.startsWith("assign") ||
            methodName.startsWith("add")
    }
}
