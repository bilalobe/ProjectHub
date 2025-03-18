package com.projecthub.infrastructure.project.adapter.event

import com.projecthub.application.project.event.ProjectEvent
import com.projecthub.application.project.port.event.ProjectEventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

/**
 * Adapter implementation for publishing project domain events
 * Uses Spring's ApplicationEventPublisher for event distribution
 */
@Component
class ProjectEventPublisherAdapter(
    private val applicationEventPublisher: ApplicationEventPublisher
) : ProjectEventPublisher {

    override fun publish(event: ProjectEvent) {
        // Publish event locally
        applicationEventPublisher.publishEvent(event)

        // TODO: Integrate with a distributed event system (e.g., Kafka, RabbitMQ)
    }
}
