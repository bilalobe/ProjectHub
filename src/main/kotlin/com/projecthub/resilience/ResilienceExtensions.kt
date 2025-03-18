package com.projecthub.resilience

import kotlinx.coroutines.withTimeout
import kotlin.time.Duration

/**
 * Extension functions for more concise resilience pattern usage
 */

/**
 * Execute a suspending function with retry capability
 */
suspend fun <T> withRetry(
    name: String = "defaultRetry",
    maxAttempts: Int = 3,
    initialDelay: Duration = Duration.milliseconds(100),
    maxDelay: Duration = Duration.milliseconds(1000),
    backoffMultiplier: Double = 2.0,
    retryPredicate: ((Exception) -> Boolean)? = null,
    resilience: ResilienceService,
    block: suspend () -> T
): T {
    val config = RetryConfig(
        maxAttempts = maxAttempts,
        initialDelay = initialDelay,
        maxDelay = maxDelay,
        backoffMultiplier = backoffMultiplier,
        retryPredicate = retryPredicate
    )
    return resilience.retry(name, config, block)
}

/**
 * Execute a suspending function with circuit breaker protection
 */
suspend fun <T> withCircuitBreaker(
    name: String,
    failureThreshold: Int = 5,
    resetTimeout: Duration = Duration.seconds(30),
    successThreshold: Int = 2,
    resilience: ResilienceService,
    block: suspend () -> T
): T {
    val config = CircuitBreakerConfig(
        failureThreshold = failureThreshold,
        resetTimeout = resetTimeout,
        successThreshold = successThreshold
    )
    return resilience.circuitBreaker(name, config, block)
}

/**
 * Execute a suspending function with bulkhead protection
 */
suspend fun <T> withBulkhead(
    name: String,
    maxConcurrentCalls: Int = 25,
    resilience: ResilienceService,
    block: suspend () -> T
): T {
    val config = BulkheadConfig(maxConcurrentCalls = maxConcurrentCalls)
    return resilience.bulkhead(name, config, block)
}

/**
 * Combine multiple resilience patterns
 */
suspend fun <T> withResilience(
    name: String,
    timeout: Duration? = null,
    retryConfig: RetryConfig? = null,
    circuitBreakerConfig: CircuitBreakerConfig? = null,
    bulkheadConfig: BulkheadConfig? = null,
    resilience: ResilienceService,
    block: suspend () -> T
): T {
    val executeBlock: suspend () -> T = {
        if (timeout != null) {
            withTimeout(timeout) {
                block()
            }
        } else {
            block()
        }
    }
    
    return if (bulkheadConfig != null) {
        resilience.bulkhead(name + "Bulkhead", bulkheadConfig) {
            if (circuitBreakerConfig != null) {
                resilience.circuitBreaker(name + "CircuitBreaker", circuitBreakerConfig) {
                    if (retryConfig != null) {
                        resilience.retry(name + "Retry", retryConfig, executeBlock)
                    } else {
                        executeBlock()
                    }
                }
            } else if (retryConfig != null) {
                resilience.retry(name + "Retry", retryConfig, executeBlock)
            } else {
                executeBlock()
            }
        }
    } else if (circuitBreakerConfig != null) {
        resilience.circuitBreaker(name + "CircuitBreaker", circuitBreakerConfig) {
            if (retryConfig != null) {
                resilience.retry(name + "Retry", retryConfig, executeBlock)
            } else {
                executeBlock()
            }
        }
    } else if (retryConfig != null) {
        resilience.retry(name + "Retry", retryConfig, executeBlock)
    } else {
        executeBlock()
    }
}
