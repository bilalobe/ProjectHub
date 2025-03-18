package com.projecthub.infrastructure.resilience.milestone

import com.projecthub.domain.milestone.Milestone
import com.projecthub.infrastructure.persistence.milestone.JpaMilestoneRepository
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.dao.TransientDataAccessException
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class MilestoneResilienceTest {

    @Mock
    private lateinit var milestoneRepository: JpaMilestoneRepository

    private lateinit var meterRegistry: MeterRegistry
    private lateinit var circuitBreaker: CircuitBreaker
    private lateinit var retryAspect: MilestoneRetryAspect
    private lateinit var metricsAspect: MilestoneMetricsAspect

    @BeforeEach
    fun setup() {
        meterRegistry = SimpleMeterRegistry()
        circuitBreaker = CircuitBreaker.ofDefaults("test-breaker")
        retryAspect = MilestoneRetryAspect()
        metricsAspect = MilestoneMetricsAspect(meterRegistry)
    }

    @Test
    fun `test retry mechanism on transient failure`() {
        val milestoneId = UUID.randomUUID().toString()

        `when`(milestoneRepository.findById(milestoneId))
            .thenThrow(TransientDataAccessException("DB timeout"))
            .thenThrow(TransientDataAccessException("DB timeout"))
            .thenReturn(Optional.of(Milestone(milestoneId)))

        val decoratedRepo = RetryDecorator(milestoneRepository, retryAspect)
        val result = decoratedRepo.findById(milestoneId)

        assertTrue(result.isPresent)
        verify(milestoneRepository, times(3)).findById(milestoneId)
    }

    @Test
    fun `test circuit breaker opens after failures`() {
        val milestoneId = UUID.randomUUID().toString()
        val exception = RuntimeException("Service unavailable")

        `when`(milestoneRepository.findById(milestoneId))
            .thenThrow(exception)

        val decoratedRepo = CircuitBreakerDecorator(milestoneRepository, circuitBreaker)

        // Generate failures to open circuit
        repeat(5) {
            try {
                decoratedRepo.findById(milestoneId)
            } catch (e: Exception) {
                // Expected
            }
        }

        assertTrue(circuitBreaker.state == CircuitBreaker.State.OPEN)
    }

    @Test
    fun `test metrics collection for successful operation`() {
        val milestoneId = UUID.randomUUID().toString()
        val milestone = Milestone(milestoneId)

        `when`(milestoneRepository.findById(milestoneId))
            .thenReturn(Optional.of(milestone))

        val decoratedRepo = MetricsDecorator(milestoneRepository, metricsAspect)
        decoratedRepo.findById(milestoneId)

        val counter = meterRegistry.get("milestone.operation.total")
            .tag("operation", "findById")
            .tag("status", "success")
            .counter()

        assertEquals(1.0, counter.count())
    }

    @Test
    fun `test metrics collection for failed operation`() {
        val milestoneId = UUID.randomUUID().toString()
        val exception = RuntimeException("Operation failed")

        `when`(milestoneRepository.findById(milestoneId))
            .thenThrow(exception)

        val decoratedRepo = MetricsDecorator(milestoneRepository, metricsAspect)

        try {
            decoratedRepo.findById(milestoneId)
        } catch (e: Exception) {
            // Expected
        }

        val counter = meterRegistry.get("milestone.operation.total")
            .tag("operation", "findById")
            .tag("status", "error")
            .tag("error_type", "RuntimeException")
            .counter()

        assertEquals(1.0, counter.count())
    }
}

// Test helper classes
private class RetryDecorator(
    private val repository: JpaMilestoneRepository,
    private val retryAspect: MilestoneRetryAspect
) : JpaMilestoneRepository by repository

private class CircuitBreakerDecorator(
    private val repository: JpaMilestoneRepository,
    private val circuitBreaker: CircuitBreaker
) : JpaMilestoneRepository by repository

private class MetricsDecorator(
    private val repository: JpaMilestoneRepository,
    private val metricsAspect: MilestoneMetricsAspect
) : JpaMilestoneRepository by repository
