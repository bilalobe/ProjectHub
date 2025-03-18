package com.projecthub.events.modulith

import com.projecthub.events.DomainEvent
import com.projecthub.events.EventPublisher
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Primary
import org.springframework.modulith.events.EventPublicationRegistry
import org.springframework.stereotype.Component

/**
 * Implementation of EventPublisher that uses Spring Modulith to publish events.
 * This enables structured, transactional event handling across module boundaries.
 */
@Component
@Primary
class ModulithEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val eventPublicationRegistry: EventPublicationRegistry
) : EventPublisher {
    private val logger = LoggerFactory.getLogger(ModulithEventPublisher::class.java)

    override suspend fun publish(event: DomainEvent) {
        try {
            logger.debug("Publishing event: ${event.type} with ID: ${event.eventId}")
            applicationEventPublisher.publishEvent(event)
        } catch (e: Exception) {
            logger.error("Failed to publish event: ${event.type}", e)
            throw e
        }
    }

    override suspend fun publishAll(events: List<DomainEvent>) {
        events.forEach { publish(it) }
    }
}
