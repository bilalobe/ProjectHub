package com.projecthub.events.sourcing

import com.projecthub.events.DomainEvent

/**
 * Interface for aggregates that can be reconstructed from a series of events.
 * This is the foundation for event sourcing.
 */
interface EventSourcingAggregate<T> {
    /**
     * Apply an event to the aggregate, updating its state.
     */
    fun apply(event: DomainEvent): T

    /**
     * Load the aggregate state from a series of historical events.
     */
    fun loadFromHistory(events: List<DomainEvent>): T
}
