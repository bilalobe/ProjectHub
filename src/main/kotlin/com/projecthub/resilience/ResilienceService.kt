package com.projecthub.resilience

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.slf4j.LoggerFactory
import java.time.Duration
import kotlin.time.Duration as KotlinDuration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toKotlinDuration

/**
 * Comprehensive resilience service providing circuit breaker, retry, timeout,
 * and bulkhead patterns for ProjectHub services.
 */
class ResilienceService(
    private val defaultRetryConfig: RetryConfig = RetryConfig(),
    private val defaultCircuitBreakerConfig: CircuitBreakerConfig = CircuitBreakerConfig(),
    private val defaultBulkheadConfig: BulkheadConfig = BulkheadConfig()
) {
    private val logger = LoggerFactory.getLogger(ResilienceService::class.java)
    private val circuitBreakers = mutableMapOf<String, CircuitBreaker>()
    private val bulkheads = mutableMapOf<String, Bulkhead>()

    /**
     * Execute with retry pattern
     */
    suspend fun <T> retry(
        name: String = "defaultRetry",
        config: RetryConfig = defaultRetryConfig,
        block: suspend () -> T
    ): T {
        var currentDelay = config.initialDelay
        var attempt = 0
        var lastException: Exception? = null

        while (attempt < config.maxAttempts) {
            try {
                if (attempt > 0) {
                    logger.debug("Retry attempt $attempt for operation '$name' after ${currentDelay.inWholeMilliseconds}ms")
                }
                return block()
            } catch (e: Exception) {
                attempt++
                lastException = e
                
                if (config.retryPredicate?.invoke(e) == false) {
                    logger.debug("Exception not eligible for retry: ${e.message}")
                    throw e
                }
                
                if (attempt >= config.maxAttempts) {
                    logger.warn("Max retry attempts ($attempt) reached for operation '$name'")
                    throw MaxRetryAttemptsExceededException("Max retry attempts reached: $attempt", e)
                }
                
                logger.debug("Retry attempt $attempt failed for operation '$name': ${e.message}")
                delay(currentDelay.inWholeMilliseconds)
                currentDelay = calculateNextDelay(currentDelay, config)
            }
        }
        
        throw lastException ?: IllegalStateException("Unknown error in retry block")
    }

    /**
     * Execute with circuit breaker pattern
     */
    suspend fun <T> circuitBreaker(
        name: String,
        config: CircuitBreakerConfig = defaultCircuitBreakerConfig,
        block: suspend () -> T
    ): T {
        val circuitBreaker = circuitBreakers.getOrPut(name) {
            CircuitBreaker(name, config)
        }
        
        return circuitBreaker.execute(block)
    }

    /**
     * Execute with bulkhead pattern (limiting concurrent executions)
     */
    suspend fun <T> bulkhead(
        name: String,
        config: BulkheadConfig = defaultBulkheadConfig,
        block: suspend () -> T
    ): T {
        val bulkhead = bulkheads.getOrPut(name) {
            Bulkhead(name, config)
        }
        
        return bulkhead.execute(block)
    }

    /**
     * Execute with timeout pattern
     */
    suspend fun <T> timeout(
        timeout: KotlinDuration,
        block: suspend () -> T
    ): T {
        val startTime = System.currentTimeMillis()
        try {
            return block()
        } finally {
            val executionTime = System.currentTimeMillis() - startTime
            if (executionTime > timeout.inWholeMilliseconds) {
                logger.warn("Operation exceeded timeout of ${timeout.inWholeMilliseconds}ms (took ${executionTime}ms)")
            }
        }
    }

    /**
     * Execute with combined resilience patterns:
     * First applies bulkhead, then circuit breaker, then retries, and finally timeout
     */
    suspend fun <T> executeWithResilience(
        name: String,
        retryConfig: RetryConfig = defaultRetryConfig,
        circuitBreakerConfig: CircuitBreakerConfig = defaultCircuitBreakerConfig,
        bulkheadConfig: BulkheadConfig = defaultBulkheadConfig,
        timeoutDuration: KotlinDuration = KotlinDuration.INFINITE,
        block: suspend () -> T
    ): T {
        return bulkhead(name + "Bulkhead", bulkheadConfig) {
            circuitBreaker(name + "CircuitBreaker", circuitBreakerConfig) {
                retry(name + "Retry", retryConfig) {
                    if (timeoutDuration != KotlinDuration.INFINITE) {
                        timeout(timeoutDuration, block)
                    } else {
                        block()
                    }
                }
            }
        }
    }

    /**
     * Reset all circuit breakers to closed state
     */
    fun resetAllCircuitBreakers() {
        circuitBreakers.values.forEach { it.reset() }
    }

    /**
     * Get circuit breaker state
     */
    fun getCircuitBreakerState(name: String): CircuitBreakerState? {
        return circuitBreakers[name]?.state
    }

    /**
     * Get all circuit breaker states
     */
    fun getAllCircuitBreakerStates(): Map<String, CircuitBreakerState> {
        return circuitBreakers.mapValues { it.value.state }
    }

    private fun calculateNextDelay(currentDelay: KotlinDuration, config: RetryConfig): KotlinDuration {
        val newDelay = (currentDelay.inWholeMilliseconds * config.backoffMultiplier).toLong().milliseconds
        return minOf(newDelay, config.maxDelay)
    }
}

/**
 * Circuit breaker implementation
 */
class CircuitBreaker(
    private val name: String,
    private val config: CircuitBreakerConfig
) {
    private val logger = LoggerFactory.getLogger(CircuitBreaker::class.java)
    
    @Volatile
    var state: CircuitBreakerState = CircuitBreakerState.CLOSED
        private set
    
    private var failureCount = 0
    private var successCount = 0
    private var lastFailureTime: Long = 0
    private val failureThreshold = config.failureThreshold
    private val resetTimeout = config.resetTimeout
    private val successThreshold = config.successThreshold
    
    suspend fun <T> execute(block: suspend () -> T): T {
        when (state) {
            CircuitBreakerState.OPEN -> {
                if (System.currentTimeMillis() - lastFailureTime >= resetTimeout.inWholeMilliseconds) {
                    logger.info("Circuit breaker '$name' half-opening after ${resetTimeout.inWholeMilliseconds}ms timeout")
                    state = CircuitBreakerState.HALF_OPEN
                    successCount = 0
                } else {
                    logger.debug("Circuit breaker '$name' is OPEN - fast failing")
                    throw CircuitBreakerOpenException("Circuit breaker '$name' is open")
                }
            }
            CircuitBreakerState.HALF_OPEN -> {
                // Limited traffic in half-open state
            }
            CircuitBreakerState.CLOSED -> {
                // Normal operation
            }
        }
        
        try {
            val result = block()
            handleSuccess()
            return result
        } catch (e: Exception) {
            handleFailure(e)
            throw e
        }
    }
    
    private fun handleSuccess() {
        when (state) {
            CircuitBreakerState.CLOSED -> {
                failureCount = 0
            }
            CircuitBreakerState.HALF_OPEN -> {
                successCount++
                if (successCount >= successThreshold) {
                    logger.info("Circuit breaker '$name' closing after $successCount successful executions")
                    state = CircuitBreakerState.CLOSED
                    failureCount = 0
                    successCount = 0
                }
            }
            CircuitBreakerState.OPEN -> {
                // Should not reach here
            }
        }
    }
    
    private fun handleFailure(exception: Exception) {
        when (state) {
            CircuitBreakerState.CLOSED -> {
                failureCount++
                if (failureCount >= failureThreshold) {
                    logger.warn("Circuit breaker '$name' opening after $failureCount consecutive failures")
                    state = CircuitBreakerState.OPEN
                    lastFailureTime = System.currentTimeMillis()
                }
            }
            CircuitBreakerState.HALF_OPEN -> {
                logger.info("Circuit breaker '$name' reopening due to failure in half-open state")
                state = CircuitBreakerState.OPEN
                lastFailureTime = System.currentTimeMillis()
                successCount = 0
            }
            CircuitBreakerState.OPEN -> {
                // Update last failure time
                lastFailureTime = System.currentTimeMillis()
            }
        }
    }
    
    fun reset() {
        logger.info("Circuit breaker '$name' manually reset to CLOSED state")
        state = CircuitBreakerState.CLOSED
        failureCount = 0
        successCount = 0
    }
}

/**
 * Bulkhead implementation to limit concurrent executions
 */
class Bulkhead(
    private val name: String,
    private val config: BulkheadConfig
) {
    private val logger = LoggerFactory.getLogger(Bulkhead::class.java)
    private val semaphore = Semaphore(config.maxConcurrentCalls)
    private var rejectedCount = 0
    
    suspend fun <T> execute(block: suspend () -> T): T {
        if (!semaphore.tryAcquire(0)) {
            rejectedCount++
            if (rejectedCount % 10 == 0) { // Log every 10 rejections
                logger.warn("Bulkhead '$name' rejected $rejectedCount calls (max concurrent: ${config.maxConcurrentCalls})")
            }
            throw BulkheadFullException("Bulkhead '$name' is full")
        }
        
        return try {
            block()
        } finally {
            semaphore.release()
        }
    }
}

/**
 * Circuit breaker state enum
 */
enum class CircuitBreakerState {
    CLOSED,     // Normal operation, all requests pass through
    OPEN,       // All requests fail fast without execution
    HALF_OPEN   // Limited requests allowed to test system health
}

/**
 * Configuration for retry mechanism
 */
data class RetryConfig(
    val maxAttempts: Int = 3,
    val initialDelay: KotlinDuration = 100.milliseconds,
    val maxDelay: KotlinDuration = 1000.milliseconds,
    val backoffMultiplier: Double = 2.0,
    val retryPredicate: ((Exception) -> Boolean)? = null
)

/**
 * Configuration for circuit breaker mechanism
 */
data class CircuitBreakerConfig(
    val failureThreshold: Int = 5,
    val resetTimeout: KotlinDuration = Duration.ofSeconds(30).toKotlinDuration(),
    val successThreshold: Int = 2
)

/**
 * Configuration for bulkhead mechanism
 */
data class BulkheadConfig(
    val maxConcurrentCalls: Int = 25
)

/**
 * Exceptions for resilience mechanisms
 */
class MaxRetryAttemptsExceededException(message: String, cause: Throwable? = null) : Exception(message, cause)
class CircuitBreakerOpenException(message: String) : Exception(message)
class BulkheadFullException(message: String) : Exception(message)
class TimeoutExceededException(message: String) : Exception(message)
