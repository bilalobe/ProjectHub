package com.projecthub.domain.project.events

import com.projecthub.events.DomainEvent
import java.time.Instant
import java.util.UUID

/**
 * Event representing project creation.
 */
class ProjectCreatedEvent(
    override val aggregateId: UUID,
    val name: String,
    val description: String,
    val startDate: Instant,
    val dueDate: Instant? = null,
    val ownerId: UUID
) : DomainEvent() {
    override val type: String = "ProjectCreatedEvent"
}
