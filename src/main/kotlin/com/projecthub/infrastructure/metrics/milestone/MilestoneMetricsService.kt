package com.projecthub.infrastructure.metrics.milestone

import com.projecthub.domain.milestone.event.*
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Service
class MilestoneMetricsService(private val meterRegistry: MeterRegistry) {

    private val milestoneCreationTimes = ConcurrentHashMap<String, LocalDateTime>()

    @EventListener
    fun handleMilestoneCreatedEvent(event: MilestoneCreatedEvent) {
        // Track milestone creation
        meterRegistry.counter(
            "milestone.created",
            "project_id", event.projectId
        ).increment()

        // Store creation time for completion duration tracking
        milestoneCreationTimes[event.milestoneId] = LocalDateTime.now()
    }

    @EventListener
    fun handleMilestoneCompletedEvent(event: MilestoneCompletedEvent) {
        // Track milestone completion
        meterRegistry.counter(
            "milestone.completed",
            "project_id", event.projectId
        ).increment()

        // Calculate and record completion duration
        milestoneCreationTimes[event.milestoneId]?.let { creationTime ->
            val duration = ChronoUnit.SECONDS.between(
                creationTime,
                LocalDateTime.now()
            )

            Timer.builder("milestone.completion.duration")
                .tag("project_id", event.projectId)
                .register(meterRegistry)
                .record(duration, TimeUnit.SECONDS)

            milestoneCreationTimes.remove(event.milestoneId)
        }
    }

    @EventListener
    fun handleMilestoneAssignedEvent(event: MilestoneAssignedEvent) {
        // Track milestone assignments
        meterRegistry.counter(
            "milestone.assigned",
            "project_id", event.projectId,
            "assignee_id", event.assigneeId
        ).increment()
    }

    @EventListener
    fun handleMilestoneDueDateChangedEvent(event: MilestoneDueDateChangedEvent) {
        // Track due date changes
        meterRegistry.counter(
            "milestone.duedate.changed",
            "project_id", event.projectId
        ).increment()
    }
}
