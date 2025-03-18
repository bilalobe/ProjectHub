package com.projecthub.infrastructure.resource.adapter.event

import com.projecthub.application.resource.event.ResourceEvent
import com.projecthub.application.resource.port.event.ResourceEventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

/**
 * Adapter implementation for publishing resource domain events
 * Uses Spring's ApplicationEventPublisher for event distribution
 */
@Component
class ResourceEventPublisherAdapter(
    private val applicationEventPublisher: ApplicationEventPublisher
) : ResourceEventPublisher {

    override fun publish(event: ResourceEvent) {
        // Publish event locally
        applicationEventPublisher.publishEvent(event)

        // TODO: Integrate with a distributed event system (e.g., Kafka, RabbitMQ)
    }
}
