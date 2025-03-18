package com.projecthub.resilience

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * Collects metrics for resilience operations to monitor system health
 */
class ResilienceMetricsCollector(private val registry: MeterRegistry) {
    private val retryCounters = ConcurrentHashMap<String, Counter>()
    private val circuitBreakerCounters = ConcurrentHashMap<String, Map<CircuitBreakerState, Counter>>()
    private val bulkheadRejectionCounters = ConcurrentHashMap<String, Counter>()
    private val operationTimers = ConcurrentHashMap<String, Timer>()
    
    /**
     * Record a retry attempt for an operation
     */
    fun recordRetryAttempt(operationName: String, attempt: Int) {
        val counterName = "resilience.retry.attempt"
        val counter = retryCounters.computeIfAbsent(operationName) {
            Counter.builder(counterName)
                .tag("operation", operationName)
                .register(registry)
        }
        counter.increment()
    }
    
    /**
     * Record a circuit breaker state change
     */
    fun recordCircuitBreakerStateChange(name: String, state: CircuitBreakerState) {
        val countersForBreaker = circuitBreakerCounters.computeIfAbsent(name) {
            CircuitBreakerState.values().associate { cbs ->
                cbs to Counter.builder("resilience.circuitbreaker.state")
                    .tag("name", name)
                    .tag("state", cbs.name)
                    .register(registry)
            }
        }
        
        countersForBreaker[state]?.increment()
    }
    
    /**
     * Record a bulkhead rejection
     */
    fun recordBulkheadRejection(name: String) {
        val counter = bulkheadRejectionCounters.computeIfAbsent(name) {
            Counter.builder("resilience.bulkhead.rejection")
                .tag("name", name)
                .register(registry)
        }
        counter.increment()
    }
    
    /**
     * Record operation execution time
     */
    fun recordOperationTime(name: String, durationMs: Long) {
        val timer = operationTimers.computeIfAbsent(name) {
            Timer.builder("resilience.operation.duration")
                .tag("operation", name)
                .register(registry)
        }
        timer.record(durationMs, TimeUnit.MILLISECONDS)
    }
    
    /**
     * Record operation success
     */
    fun recordOperationSuccess(name: String) {
        Counter.builder("resilience.operation.result")
            .tag("operation", name)
            .tag("result", "success")
            .register(registry)
            .increment()
    }
    
    /**
     * Record operation failure
     */
    fun recordOperationFailure(name: String, exceptionClass: String) {
        Counter.builder("resilience.operation.result")
            .tag("operation", name)
            .tag("result", "failure")
            .tag("exception", exceptionClass)
            .register(registry)
            .increment()
    }
}
