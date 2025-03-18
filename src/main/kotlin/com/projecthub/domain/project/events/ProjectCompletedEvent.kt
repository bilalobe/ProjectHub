package com.projecthub.domain.project.events

import com.projecthub.events.DomainEvent
import java.time.Instant
import java.util.UUID

/**
 * Event representing project completion.
 */
class ProjectCompletedEvent(
    override val aggregateId: UUID,
    val completedAt: Instant
) : DomainEvent() {
    override val type: String = "ProjectCompletedEvent"
}
