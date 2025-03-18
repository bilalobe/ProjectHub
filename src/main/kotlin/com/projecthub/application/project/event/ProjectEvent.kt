package com.projecthub.application.project.event

import com.projecthub.application.project.domain.ProjectStatus
import java.time.LocalDateTime

/**
 * Base class for all project domain events
 */
sealed class ProjectEvent(
    val projectId: String,
    val occurredAt: LocalDateTime = LocalDateTime.now(),
    val version: String = "1.0" // Added versioning for event compatibility
)

data class ProjectCreatedEvent(
    val id: String,
    val name: String,
    val description: String,
    val ownerId: String,
    val teamId: String
) : ProjectEvent(id)

data class ProjectUpdatedEvent(
    val id: String,
    val updatedFields: Map<String, Any?>
) : ProjectEvent(id)

data class ProjectStatusChangedEvent(
    val id: String,
    val previousStatus: ProjectStatus,
    val newStatus: ProjectStatus
) : ProjectEvent(id)

data class ProjectTeamAssignedEvent(
    val id: String,
    val previousTeamId: String?,
    val newTeamId: String
) : ProjectEvent(id)

data class ProjectDeletedEvent(
    val id: String
) : ProjectEvent(id)
