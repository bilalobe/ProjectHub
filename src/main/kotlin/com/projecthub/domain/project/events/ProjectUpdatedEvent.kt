package com.projecthub.domain.project.events

import com.projecthub.events.DomainEvent
import java.time.Instant
import java.util.UUID

/**
 * Event representing project update.
 */
class ProjectUpdatedEvent(
    override val aggregateId: UUID,
    val name: String,
    val description: String,
    val startDate: Instant,
    val dueDate: Instant? = null
) : DomainEvent() {
    override val type: String = "ProjectUpdatedEvent"
}
