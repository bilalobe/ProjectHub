package com.projecthub.infrastructure.messaging.cohort

import com.projecthub.application.project.port.`in`.ProjectManagementUseCase
import com.projecthub.application.project.dto.CreateProjectCommand
import com.projecthub.domain.cohort.event.CohortCreatedEvent
import com.projecthub.domain.cohort.event.CohortCompletedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

/**
 * Event listener that responds to cohort domain events
 * This component demonstrates cross-feature integration via domain events
 */
@Component
class CohortEventListener(private val projectManagementUseCase: ProjectManagementUseCase) {

    /**
     * When a new cohort is created, automatically create a default project for it
     */
    @EventListener
    fun handleCohortCreatedEvent(event: CohortCreatedEvent) {
        // Create a default project for the new cohort
        val command = CreateProjectCommand(
            name = "Default Project for ${event.name}",
            description = "Automatically generated project for the cohort: ${event.name}",
            ownerId = "system" // You might want to take this from a service or config
        )

        projectManagementUseCase.createProject(command)
    }

    /**
     * When a cohort is completed, mark all its projects as completed too
     */
    @EventListener
    fun handleCohortCompletedEvent(event: CohortCompletedEvent) {
        // In a real implementation, you would:
        // 1. Find all projects associated with this cohort
        // 2. Mark each as completed

        // For demonstration purposes only - assuming we have a way to get projects by cohort:
        // projectRepository.findByCohortId(event.cohortId).forEach {
        //     projectManagementUseCase.completeProject(it.id)
        // }
    }
}
