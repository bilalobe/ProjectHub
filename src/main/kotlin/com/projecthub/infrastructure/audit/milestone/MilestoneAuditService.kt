package com.projecthub.infrastructure.audit.milestone

import com.projecthub.domain.milestone.event.*
import com.projecthub.infrastructure.audit.AuditEntry
import com.projecthub.infrastructure.audit.AuditRepository
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MilestoneAuditService(private val auditRepository: AuditRepository) {

    @EventListener
    fun handleMilestoneCreatedEvent(event: MilestoneCreatedEvent) {
        auditRepository.save(
            AuditEntry(
                entityType = "milestone",
                entityId = event.milestoneId,
                action = "created",
                details = "Milestone '${event.name}' created for project ${event.projectId}",
                timestamp = LocalDateTime.now(),
                metadata = mapOf(
                    "project_id" to event.projectId,
                    "milestone_name" to event.name
                )
            )
        )
    }

    @EventListener
    fun handleMilestoneCompletedEvent(event: MilestoneCompletedEvent) {
        auditRepository.save(
            AuditEntry(
                entityType = "milestone",
                entityId = event.milestoneId,
                action = "completed",
                details = "Milestone completed in project ${event.projectId}",
                timestamp = LocalDateTime.now(),
                metadata = mapOf(
                    "project_id" to event.projectId,
                    "completion_date" to event.completionDate
                )
            )
        )
    }

    @EventListener
    fun handleMilestoneAssignedEvent(event: MilestoneAssignedEvent) {
        auditRepository.save(
            AuditEntry(
                entityType = "milestone",
                entityId = event.milestoneId,
                action = "assigned",
                details = "Milestone assigned to user ${event.assigneeId}",
                timestamp = LocalDateTime.now(),
                metadata = mapOf(
                    "project_id" to event.projectId,
                    "assignee_id" to event.assigneeId
                )
            )
        )
    }

    @EventListener
    fun handleMilestoneDueDateChangedEvent(event: MilestoneDueDateChangedEvent) {
        auditRepository.save(
            AuditEntry(
                entityType = "milestone",
                entityId = event.milestoneId,
                action = "due_date_changed",
                details = "Milestone due date changed from ${event.oldDueDate} to ${event.newDueDate}",
                timestamp = LocalDateTime.now(),
                metadata = mapOf(
                    "project_id" to event.projectId,
                    "old_due_date" to event.oldDueDate,
                    "new_due_date" to event.newDueDate
                )
            )
        )
    }
}
