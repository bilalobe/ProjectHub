package com.projecthub.domain.project.workflow.action

import com.projecthub.domain.project.Project
import com.projecthub.domain.project.ProjectRepository
import com.projecthub.domain.project.event.ProjectStatusChangedEvent
import com.projecthub.domain.workflow.TransitionAction
import com.projecthub.domain.workflow.WorkflowContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * Defines actions that are executed when a project transitions between workflow states.
 * These actions implement business logic that should occur as part of state transitions.
 */
@Component
class ProjectWorkflowActions(
    private val projectRepository: ProjectRepository,
    private val eventPublisher: ApplicationEventPublisher
) {

    /**
     * Updates the project start date when transitioning to an active state.
     * If the start date is not already set, it will be set to the current time.
     */
    fun updateStartDate(): TransitionAction = TransitionAction { context ->
        withContext(Dispatchers.IO) {
            val project = getProject(context)
            if (project.startDate == null) {
                project.startDate = LocalDateTime.now()
                projectRepository.save(project)
            }
        }
    }

    /**
     * Updates the project completion date when transitioning to a completed state.
     * Sets the completion date to the current time.
     */
    fun updateCompletionDate(): TransitionAction = TransitionAction { context ->
        withContext(Dispatchers.IO) {
            val project = getProject(context)
            project.completionDate = LocalDateTime.now()
            projectRepository.save(project)
        }
    }

    /**
     * Notifies interested parties when a project status changes.
     * Publishes a ProjectStatusChangedEvent to the application event system.
     */
    fun notifyProjectStatusChanged(): TransitionAction = TransitionAction { context ->
        withContext(Dispatchers.IO) {
            val project = getProject(context)
            val event = ProjectStatusChangedEvent(
                projectId = project.id,
                previousStatus = context.fromState,
                newStatus = context.toState,
                timestamp = LocalDateTime.now()
            )
            eventPublisher.publishEvent(event)
        }
    }

    /**
     * Updates the last activity timestamp for a project when any transition occurs.
     */
    fun updateLastActivity(): TransitionAction = TransitionAction { context ->
        withContext(Dispatchers.IO) {
            val project = getProject(context)
            project.lastActivityDate = LocalDateTime.now()
            projectRepository.save(project)
        }
    }

    /**
     * Automatically assigns the project to unassigned tasks when
     * transitioning to an in-progress state.
     */
    fun assignUnassignedTasks(): TransitionAction = TransitionAction { context ->
        withContext(Dispatchers.IO) {
            val project = getProject(context)
            project.autoAssignTasks()
            projectRepository.save(project)
        }
    }

    /**
     * Gets the project from the workflow context.
     */
    private suspend fun getProject(context: WorkflowContext): Project {
        return projectRepository.findById(context.entityId)
            ?: throw IllegalStateException("Project not found with ID: ${context.entityId}")
    }
}