package com.projecthub.events.modulith

import com.projecthub.events.DomainEvent
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.modulith.events.EventPublicationRegistry
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

/**
 * Component to track and log event publications across the system.
 * Integrates with Spring Modulith's event publication registry.
 */
@Component
class EventPublicationTracker(
    private val eventPublicationRegistry: EventPublicationRegistry
) {
    private val logger = LoggerFactory.getLogger(EventPublicationTracker::class.java)

    /**
     * Listen for all domain events and log them.
     */
    @EventListener
    @Async
    fun onDomainEvent(event: DomainEvent) {
        logger.debug("Event processed: ${event.type} with ID: ${event.eventId}")

        // You can add additional tracking or logging here
        // For example, update metrics, notify monitoring systems, etc.
    }

    /**
     * Check for completion of event publications.
     */
    fun checkCompletionStatus(eventId: String): Boolean {
        return eventPublicationRegistry.findIncompletePublications().none { it.id == eventId }
    }
}
