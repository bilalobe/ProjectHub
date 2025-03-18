package com.projecthub.events.sourcing

import com.projecthub.domain.BaseAggregateRoot
import com.projecthub.events.DomainEvent
import org.slf4j.LoggerFactory

/**
 * Base class for aggregates that support event sourcing.
 * Provides common functionality for applying events and loading from history.
 */
abstract class AbstractEventSourcingAggregate : BaseAggregateRoot(),
    EventSourcingAggregate<AbstractEventSourcingAggregate> {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Apply an event to this aggregate, updating its state and registering the event.
     */
    override fun apply(event: DomainEvent): AbstractEventSourcingAggregate {
        // Apply the event to update the aggregate state
        applyEvent(event)
        // Register the event for later dispatching
        registerEvent(event)
        return this
    }

    /**
     * Load the aggregate state from a series of historical events.
     */
    override fun loadFromHistory(events: List<DomainEvent>): AbstractEventSourcingAggregate {
        events.forEach { event ->
            try {
                // Apply each event to the aggregate without registering it
                applyEvent(event)
            } catch (e: Exception) {
                logger.error("Failed to apply event ${event.type} to aggregate", e)
                throw EventSourcingException("Failed to apply event ${event.type} to aggregate", e)
            }
        }
        return this
    }

    /**
     * Apply an event to update the aggregate state.
     * This method should be implemented by subclasses to handle specific events.
     */
    protected abstract fun applyEvent(event: DomainEvent)
}

/**
 * Exception thrown when there's an error in event sourcing operations.
 */
class EventSourcingException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
