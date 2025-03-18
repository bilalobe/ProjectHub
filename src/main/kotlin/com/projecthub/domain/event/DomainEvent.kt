package com.projecthub.domain.event

import java.time.Instant
import java.util.UUID

/**
 * Base class for all domain events in the system.
 * Domain events represent significant state changes within a domain context.
 */
abstract class DomainEvent {
    val eventId: String = UUID.randomUUID().toString()
    val timestamp: Instant = Instant.now()

    /**
     * Unique event type identifier, typically the simple class name
     */
    val eventType: String = this.javaClass.simpleName
}
