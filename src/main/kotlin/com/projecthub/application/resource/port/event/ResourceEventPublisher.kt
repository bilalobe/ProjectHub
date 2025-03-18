package com.projecthub.application.resource.port.event

import com.projecthub.application.resource.event.ResourceEvent

/**
 * Port for publishing resource domain events
 * Technology-agnostic interface for event distribution
 */
interface ResourceEventPublisher {
    fun publish(event: ResourceEvent)
}
