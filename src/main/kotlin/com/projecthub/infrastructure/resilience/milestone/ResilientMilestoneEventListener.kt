package com.projecthub.infrastructure.resilience.milestone

import com.projecthub.domain.milestone.event.*
import com.projecthub.infrastructure.messaging.milestone.MilestoneEventListener
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ResilientMilestoneEventListener(
    private val delegate: MilestoneEventListener,
    private val projectServiceBreaker: CircuitBreaker,
    private val taskServiceBreaker: CircuitBreaker
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun handleMilestoneCompletedEvent(event: MilestoneCompletedEvent) {
        // Update project progress with circuit breaker
        projectServiceBreaker.executeRunnable {
            try {
                delegate.handleMilestoneCompletedEvent(event)
            } catch (e: Exception) {
                logger.error("Failed to process milestone completion for project ${event.projectId}", e)
                throw e
            }
        }
    }

    @EventListener
    fun handleMilestoneDueDateChangedEvent(event: MilestoneDueDateChangedEvent) {
        // Update task dates with circuit breaker
        taskServiceBreaker.executeRunnable {
            try {
                delegate.handleMilestoneDueDateChangedEvent(event)
            } catch (e: Exception) {
                logger.error("Failed to update task dates for milestone ${event.milestoneId}", e)
                throw e
            }
        }
    }

    @EventListener
    fun handleMilestoneAssignedEvent(event: MilestoneAssignedEvent) {
        // Update task assignments with circuit breaker
        taskServiceBreaker.executeRunnable {
            try {
                delegate.handleMilestoneAssignedEvent(event)
            } catch (e: Exception) {
                logger.error("Failed to reassign tasks for milestone ${event.milestoneId}", e)
                throw e
            }
        }
    }
}
