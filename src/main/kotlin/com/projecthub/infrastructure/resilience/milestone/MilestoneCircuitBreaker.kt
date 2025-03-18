package com.projecthub.infrastructure.resilience.milestone

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class MilestoneCircuitBreakerConfig {

    companion object {
        const val PROJECT_SERVICE_BREAKER = "projectServiceBreaker"
        const val TASK_SERVICE_BREAKER = "taskServiceBreaker"
    }

    @Bean
    fun circuitBreakerRegistry(): CircuitBreakerRegistry {
        val config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50f)
            .waitDurationInOpenState(Duration.ofSeconds(10))
            .permittedNumberOfCallsInHalfOpenState(5)
            .slidingWindowSize(10)
            .minimumNumberOfCalls(5)
            .build()

        return CircuitBreakerRegistry.of(config)
    }

    @Bean
    fun projectServiceCircuitBreaker(registry: CircuitBreakerRegistry): CircuitBreaker {
        return registry.circuitBreaker(PROJECT_SERVICE_BREAKER)
    }

    @Bean
    fun taskServiceCircuitBreaker(registry: CircuitBreakerRegistry): CircuitBreaker {
        return registry.circuitBreaker(TASK_SERVICE_BREAKER)
    }
}
