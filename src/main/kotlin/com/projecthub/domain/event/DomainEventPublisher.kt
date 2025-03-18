package com.projecthub.domain.event

/**
 * Interface for publishing domain events
 * This is a port in the domain that will be implemented by infrastructure adapters
 */
interface DomainEventPublisher {
    /**
     * Publishes a domain event
     *
     * @param event The domain event to publish
     */
    fun publish(event: DomainEvent)

    /**
     * Publishes multiple domain events
     *
     * @param events The domain events to publish
     */
    fun publishAll(events: Collection<DomainEvent>) {
        events.forEach { publish(it) }
    }
}
