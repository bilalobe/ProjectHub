@org.springframework.modulith.NamedInterface("project-infrastructure")

package com.projecthub.infrastructure.messaging.project

import com.projecthub.core.application.workflow.port.`in`.WorkflowManagementUseCase
import com.projecthub.core.domain.event.project.ProjectCreatedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ProjectEventListener(private val workflowManagementUseCase: WorkflowManagementUseCase) {

    @EventListener
    fun handleProjectCreatedEvent(event: ProjectCreatedEvent) {
        // Create default workflow for new project
        val workflowId = workflowManagementUseCase.createWorkflow("Default workflow for ${event.name}")

        // Additional cross-domain logic
    }
}
