package com.projecthub.infrastructure.messaging.milestone

import com.projecthub.application.project.port.`in`.ProjectManagementUseCase
import com.projecthub.application.task.port.`in`.TaskManagementUseCase
import com.projecthub.domain.milestone.event.*
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

/**
 * Event listener that responds to milestone domain events
 * This component demonstrates cross-feature integration via domain events
 */
@Component
class MilestoneEventListener(
    private val projectManagementUseCase: ProjectManagementUseCase,
    private val taskManagementUseCase: TaskManagementUseCase
) {

    /**
     * When a milestone is completed, update project progress and complete related tasks
     */
    @EventListener
    fun handleMilestoneCompletedEvent(event: MilestoneCompletedEvent) {
        // Update project progress
        projectManagementUseCase.recalculateProgress(event.projectId)

        // Complete any associated tasks
        taskManagementUseCase.completeTasksForMilestone(event.milestoneId)
    }

    /**
     * When a milestone due date changes, adjust dependent task dates
     */
    @EventListener
    fun handleMilestoneDueDateChangedEvent(event: MilestoneDueDateChangedEvent) {
        taskManagementUseCase.updateTaskDatesForMilestone(
            milestoneId = event.milestoneId,
            oldDueDate = event.oldDueDate,
            newDueDate = event.newDueDate
        )
    }

    /**
     * When a milestone is assigned, update associated task assignments
     */
    @EventListener
    fun handleMilestoneAssignedEvent(event: MilestoneAssignedEvent) {
        taskManagementUseCase.reassignUnassignedTasksForMilestone(
            milestoneId = event.milestoneId,
            assigneeId = event.assigneeId
        )
    }
}
