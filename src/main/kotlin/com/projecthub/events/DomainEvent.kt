package com.projecthub.events

import java.time.Instant
import java.util.*

/**
 * Base class for all domain events in the system.
 * Events represent something that happened in the domain that domain experts care about.
 */
abstract class DomainEvent(
    val eventId: UUID = UUID.randomUUID(),
    val occurredOn: Instant = Instant.now()
) {
    abstract val aggregateId: UUID
    abstract val type: String
}
