package com.projecthub.domain.milestone.event

import com.projecthub.domain.event.DomainEvent

/**
 * Event fired when a new milestone is created
 */
data class MilestoneCreatedEvent(
    val milestoneId: String,
    val name: String,
    val projectId: String,
    val dueDate: String,
    val description: String
) : DomainEvent()

/**
 * Event fired when a milestone is completed
 */
data class MilestoneCompletedEvent(
    val milestoneId: String,
    val projectId: String,
    val completionDate: String
) : DomainEvent()

/**
 * Event fired when a milestone due date is changed
 */
data class MilestoneDueDateChangedEvent(
    val milestoneId: String,
    val projectId: String,
    val oldDueDate: String,
    val newDueDate: String
) : DomainEvent()

/**
 * Event fired when a milestone is assigned to a team member
 */
data class MilestoneAssignedEvent(
    val milestoneId: String,
    val projectId: String,
    val assigneeId: String
) : DomainEvent()