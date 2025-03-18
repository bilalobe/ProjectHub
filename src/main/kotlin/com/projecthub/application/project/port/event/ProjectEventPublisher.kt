package com.projecthub.application.project.port.event

import com.projecthub.application.project.event.ProjectEvent

/**
 * Port for publishing project domain events
 * Technology-agnostic interface for event distribution
 */
interface ProjectEventPublisher {
    fun publish(event: ProjectEvent)
}
