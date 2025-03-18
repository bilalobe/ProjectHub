package com.projecthub.infrastructure.notification.milestone

import com.projecthub.domain.milestone.event.*
import com.projecthub.infrastructure.notification.NotificationService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class MilestoneNotificationService(
    private val notificationService: NotificationService
) {

    @EventListener
    fun handleMilestoneCreatedEvent(event: MilestoneCreatedEvent) {
        notificationService.notifyStakeholders(
            recipientIds = listOf(event.projectId), // Project stakeholders
            title = "New Milestone Created",
            message = "A new milestone '${event.name}' has been created"
        )
    }

    @EventListener
    fun handleMilestoneCompletedEvent(event: MilestoneCompletedEvent) {
        notificationService.notifyStakeholders(
            recipientIds = listOf(event.projectId), // Project stakeholders
            title = "Milestone Completed",
            message = "Milestone has been marked as completed on ${event.completionDate}"
        )
    }

    @EventListener
    fun handleMilestoneDueDateChangedEvent(event: MilestoneDueDateChangedEvent) {
        notificationService.notifyStakeholders(
            recipientIds = listOf(event.projectId), // Project stakeholders
            title = "Milestone Due Date Changed",
            message = "Milestone due date has changed from ${event.oldDueDate} to ${event.newDueDate}"
        )
    }

    @EventListener
    fun handleMilestoneAssignedEvent(event: MilestoneAssignedEvent) {
        notificationService.notifyStakeholders(
            recipientIds = listOf(event.assigneeId), // Assigned user
            title = "Milestone Assignment",
            message = "You have been assigned to a milestone"
        )
    }
}
