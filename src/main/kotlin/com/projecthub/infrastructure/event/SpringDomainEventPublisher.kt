package com.projecthub.infrastructure.event

import com.projecthub.domain.event.DomainEvent
import com.projecthub.domain.event.DomainEventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

/**
 * Spring-based implementation of the DomainEventPublisher interface
 * This adapter bridges our domain events to Spring's event system
 */
@Component
class SpringDomainEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) : DomainEventPublisher {

    override fun publish(event: DomainEvent) {
        applicationEventPublisher.publishEvent(event)
    }
}