package com.projecthub.infrastructure.config

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.retry.RetryConfig
import io.github.resilience4j.retry.RetryRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class ResilienceConfig {

    @Bean
    fun circuitBreakerRegistry(): CircuitBreakerRegistry {
        val config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50f)
            .waitDurationInOpenState(Duration.ofSeconds(10))
            .slidingWindowSize(10)
            .build()

        return CircuitBreakerRegistry.of(config)
    }

    @Bean
    fun retryRegistry(): RetryRegistry {
        val config = RetryConfig.custom<Any>()
            .maxAttempts(3)
            .waitDuration(Duration.ofMillis(500))
            .retryOnException { e -> e is RuntimeException }
            .build()

        return RetryRegistry.of(config)
    }

    @Bean
    fun milestoneResilience(
        circuitBreakerRegistry: CircuitBreakerRegistry,
        retryRegistry: RetryRegistry
    ) = MilestoneResilience(circuitBreakerRegistry, retryRegistry)
}

class MilestoneResilience(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    private val retryRegistry: RetryRegistry
) {
    fun getCircuitBreaker(operationName: String) =
        circuitBreakerRegistry.circuitBreaker("milestone.$operationName")

    fun getRetry(operationName: String) =
        retryRegistry.retry("milestone.$operationName")
}
