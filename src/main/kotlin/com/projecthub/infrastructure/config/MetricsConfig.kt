package com.projecthub.infrastructure.config

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MetricsConfig {

    @Bean
    fun meterRegistry(): MeterRegistry {
        return SimpleMeterRegistry()
    }

    @Bean
    fun milestoneMetrics(meterRegistry: MeterRegistry) = MilestoneMetrics(meterRegistry)
}

class MilestoneMetrics(private val registry: MeterRegistry) {

    fun recordOperationTime(operation: String, durationMs: Long) {
        registry.timer(
            "milestone.operation.duration",
            "operation", operation
        )
            .record { durationMs }
    }

    fun incrementOperationCount(operation: String, status: String) {
        registry.counter(
            "milestone.operation.total",
            "operation", operation,
            "status", status
        )
            .increment()
    }

    fun recordConcurrentOperations(operation: String, count: Int) {
        registry.gauge(
            "milestone.operation.concurrent",
            listOf(registry.tag("operation", operation)),
            count
        )
    }
}
