package com.projecthub.domain.project.event

import com.projecthub.domain.event.DomainEvent

/**
 * Event fired when a new project is created
 */
data class ProjectCreatedEvent(
    val projectId: String,
    val name: String,
    val description: String,
    val ownerId: String
) : DomainEvent()

/**
 * Event fired when a project's details are updated
 */
data class ProjectUpdatedEvent(
    val projectId: String,
    val name: String,
    val description: String
) : DomainEvent()

/**
 * Event fired when a project is assigned to a team
 */
data class ProjectAssignedEvent(
    val projectId: String,
    val teamId: String
) : DomainEvent()

/**
 * Event fired when a project is completed
 */
data class ProjectCompletedEvent(
    val projectId: String,
    val completionDate: String
) : DomainEvent()