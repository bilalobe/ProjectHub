package com.projecthub.domain.project.events

import com.projecthub.events.DomainEvent
import java.time.Instant
import java.util.UUID

/**
 * Sealed class representing all possible events related to projects.
 * This provides compile-time exhaustiveness checking when handling project events.
 */
sealed class ProjectEvent : DomainEvent() {
    abstract val initiatorId: UUID

    /**
     * Event emitted when a new project is created.
     */
    data class Created(
        override val aggregateId: UUID,
        override val initiatorId: UUID,
        val name: String,
        val description: String,
        val startDate: Instant,
        val dueDate: Instant? = null,
        val ownerId: UUID
    ) : ProjectEvent() {
        override val type: String = "ProjectCreatedEvent"
    }

    /**
     * Event emitted when a project's details are updated.
     */
    data class Updated(
        override val aggregateId: UUID,
        override val initiatorId: UUID,
        val name: String,
        val description: String,
        val startDate: Instant,
        val dueDate: Instant? = null
    ) : ProjectEvent() {
        override val type: String = "ProjectUpdatedEvent"
    }

    /**
     * Event emitted when a project is marked as completed.
     */
    data class Completed(
        override val aggregateId: UUID,
        override val initiatorId: UUID,
        val completedAt: Instant
    ) : ProjectEvent() {
        override val type: String = "ProjectCompletedEvent"
    }

    /**
     * Event emitted when a team member is added to a project.
     */
    data class TeamMemberAdded(
        override val aggregateId: UUID,
        override val initiatorId: UUID,
        val userId: UUID,
        val role: String
    ) : ProjectEvent() {
        override val type: String = "ProjectTeamMemberAddedEvent"
    }

    /**
     * Event emitted when a team member is removed from a project.
     */
    data class TeamMemberRemoved(
        override val aggregateId: UUID,
        override val initiatorId: UUID,
        val userId: UUID
    ) : ProjectEvent() {
        override val type: String = "ProjectTeamMemberRemovedEvent"
    }

    /**
     * Event emitted when a project's status is changed.
     */
    data class StatusChanged(
        override val aggregateId: UUID,
        override val initiatorId: UUID,
        val oldStatus: ProjectStatus,
        val newStatus: ProjectStatus,
        val timestamp: Instant = Instant.now()
    ) : ProjectEvent() {
        override val type: String = "ProjectStatusChangedEvent"
    }
}
