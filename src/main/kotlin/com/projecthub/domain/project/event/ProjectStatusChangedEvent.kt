package com.projecthub.domain.project.event

import java.time.LocalDateTime

/**
 * Event that is published when a project's status changes.
 * This event can be consumed by other components to react to project status changes.
 */
data class ProjectStatusChangedEvent(
    val projectId: String,
    val previousStatus: String,
    val newStatus: String,
    val timestamp: LocalDateTime
)