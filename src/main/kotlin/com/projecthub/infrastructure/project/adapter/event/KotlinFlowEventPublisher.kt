package com.projecthub.infrastructure.project.adapter.event

import com.projecthub.application.project.event.ProjectEvent
import com.projecthub.application.project.port.event.ProjectEventPublisher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.springframework.stereotype.Component

@Component
class KotlinFlowEventPublisher : ProjectEventPublisher {
    private val _events = MutableSharedFlow<ProjectEvent>()
    val events = _events.asSharedFlow()

    override fun publish(event: ProjectEvent) {
        // Emit the event to the shared flow
        _events.tryEmit(event)
    }
}
