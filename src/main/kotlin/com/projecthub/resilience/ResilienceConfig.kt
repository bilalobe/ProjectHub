package com.projecthub.resilience

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.koin.dsl.module
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Configuration for resilience patterns
 */
object ResilienceConfig {
    
    private val config: Config by lazy {
        ConfigFactory.load().getConfig("projecthub.resilience")
    }
    
    /**
     * Create a Koin module for resilience components
     */
    fun createModule(meterRegistry: MeterRegistry = SimpleMeterRegistry()) = module {
        // Default retry config
        single {
            RetryConfig(
                maxAttempts = config.getInt("retry.maxAttempts"),
                initialDelay = config.getLong("retry.initialDelayMs").milliseconds,
                maxDelay = config.getLong("retry.maxDelayMs").milliseconds,
                backoffMultiplier = config.getDouble("retry.backoffMultiplier")
            )
        }
        
        // Default circuit breaker config
        single {
            CircuitBreakerConfig(
                failureThreshold = config.getInt("circuitBreaker.failureThreshold"),
                resetTimeout = config.getLong("circuitBreaker.resetTimeoutMs").milliseconds,
                successThreshold = config.getInt("circuitBreaker.successThreshold")
            )
        }
        
        // Default bulkhead config
        single {
            BulkheadConfig(
                maxConcurrentCalls = config.getInt("bulkhead.maxConcurrentCalls")
            )
        }
        
        // Metrics collector
        single {
            ResilienceMetricsCollector(meterRegistry)
        }
        
        // Resilience service
        single {
            ResilienceService(
                defaultRetryConfig = get(),
                defaultCircuitBreakerConfig = get(),
                defaultBulkheadConfig = get()
            )
        }
    }
    
    /**
     * Create default configurations
     */
    fun createDefaultConfigurations(): Triple<RetryConfig, CircuitBreakerConfig, BulkheadConfig> {
        val retryConfig = RetryConfig(
            maxAttempts = 3,
            initialDelay = 100.milliseconds,
            maxDelay = 1.seconds,
            backoffMultiplier = 2.0
        )
        
        val circuitBreakerConfig = CircuitBreakerConfig(
            failureThreshold = 5,
            resetTimeout = 30.seconds,
            successThreshold = 2
        )
        
        val bulkheadConfig = BulkheadConfig(
            maxConcurrentCalls = 25
        )
        
        return Triple(retryConfig, circuitBreakerConfig, bulkheadConfig)
    }
}
