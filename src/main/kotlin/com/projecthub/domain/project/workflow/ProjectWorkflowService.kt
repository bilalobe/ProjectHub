package com.projecthub.domain.project.workflow

import com.projecthub.domain.project.Project
import com.projecthub.domain.project.ProjectRepository
import com.projecthub.domain.project.ProjectStatus
import com.projecthub.domain.project.workflow.config.ProjectWorkflowConfiguration
import com.projecthub.domain.security.aspect.Auditable
import com.projecthub.domain.security.audit.SecurityEventType
import com.projecthub.domain.user.UserId
import com.projecthub.domain.workflow.TransitionResult
import com.projecthub.domain.workflow.WorkflowContext
import com.projecthub.domain.workflow.WorkflowService
import com.projecthub.domain.workflow.exception.InvalidWorkflowStateException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

/**
 * Service that provides project-specific workflow functionality.
 * This service makes it easier to perform workflow transitions on projects
 * by providing project-specific methods that abstract away the generic workflow details.
 */
@Service
class ProjectWorkflowService(
    private val projectRepository: ProjectRepository,
    private val workflowService: WorkflowService
) {
    private val logger = LoggerFactory.getLogger(ProjectWorkflowService::class.java)

    /**
     * Transitions a project to a new status.
     *
     * @param projectId The ID of the project to transition
     * @param targetStatus The status to transition the project to
     * @param userId The ID of the user initiating the transition
     * @param comment Optional comment explaining the transition
     * @return The result of the transition
     */
    @Transactional
    @Auditable(type = SecurityEventType.DATA_ACCESS, message = "Project status transition")
    suspend fun transitionProject(
        projectId: String,
        targetStatus: ProjectStatus,
        userId: UserId,
        comment: String? = null
    ): TransitionResult {
        val project = projectRepository.findById(projectId)
            ?: throw IllegalArgumentException("Project not found with ID: $projectId")

        val currentStatus = project.status ?: throw InvalidWorkflowStateException(
            "Project has no current status defined"
        )

        logger.info(
            "Transitioning project {} from {} to {} (initiated by user {})",
            projectId, currentStatus, targetStatus, userId.value
        )

        val context = WorkflowContext(
            entityId = projectId,
            entityType = ProjectWorkflowConfiguration.PROJECT_ENTITY_TYPE,
            userId = userId,
            fromState = currentStatus.name,
            toState = targetStatus.name,
            timestamp = Instant.now(),
            comment = comment
        )

        // Perform the transition using the workflow service
        val result = workflowService.transition(context)

        // Update the project's status after a successful transition
        project.status = targetStatus
        projectRepository.save(project)

        return result
    }

    /**
     * Gets all valid next statuses that a project can transition to from its current status.
     *
     * @param projectId The ID of the project
     * @return A list of valid next statuses
     */
    @Transactional(readOnly = true)
    suspend fun getAvailableTransitions(projectId: String): List<ProjectStatus> {
        val project = projectRepository.findById(projectId)
            ?: throw IllegalArgumentException("Project not found with ID: $projectId")

        val currentStatus = project.status ?: throw InvalidWorkflowStateException(
            "Project has no current status defined"
        )

        return workflowService.getAvailableTransitions(
            entityType = ProjectWorkflowConfiguration.PROJECT_ENTITY_TYPE,
            currentState = currentStatus.name
        ).collect { targetState ->
            ProjectStatus.valueOf(targetState)
        }
    }

    /**
     * Activates a project, transitioning it from DRAFT to PLANNING status.
     *
     * @param projectId The ID of the project to activate
     * @param userId The ID of the user initiating the activation
     * @return The result of the transition
     */
    @Transactional
    suspend fun activateProject(
        projectId: String,
        userId: UserId
    ): TransitionResult {
        return transitionProject(
            projectId = projectId,
            targetStatus = ProjectStatus.PLANNING,
            userId = userId,
            comment = "Project activation"
        )
    }

    /**
     * Starts a project, transitioning it from PLANNING to IN_PROGRESS status.
     *
     * @param projectId The ID of the project to start
     * @param userId The ID of the user initiating the start
     * @return The result of the transition
     */
    @Transactional
    suspend fun startProject(
        projectId: String,
        userId: UserId
    ): TransitionResult {
        return transitionProject(
            projectId = projectId,
            targetStatus = ProjectStatus.IN_PROGRESS,
            userId = userId,
            comment = "Project started"
        )
    }

    /**
     * Submits a project for review, transitioning it from IN_PROGRESS to REVIEW status.
     *
     * @param projectId The ID of the project to submit for review
     * @param userId The ID of the user initiating the submission
     * @return The result of the transition
     */
    @Transactional
    suspend fun submitForReview(
        projectId: String,
        userId: UserId
    ): TransitionResult {
        return transitionProject(
            projectId = projectId,
            targetStatus = ProjectStatus.REVIEW,
            userId = userId,
            comment = "Project submitted for review"
        )
    }

    /**
     * Completes a project, transitioning it from REVIEW to COMPLETED status.
     *
     * @param projectId The ID of the project to complete
     * @param userId The ID of the user initiating the completion
     * @return The result of the transition
     */
    @Transactional
    suspend fun completeProject(
        projectId: String,
        userId: UserId
    ): TransitionResult {
        return transitionProject(
            projectId = projectId,
            targetStatus = ProjectStatus.COMPLETED,
            userId = userId,
            comment = "Project completed"
        )
    }

    /**
     * Cancels a project, transitioning it to CANCELLED status.
     *
     * @param projectId The ID of the project to cancel
     * @param userId The ID of the user initiating the cancellation
     * @param reason The reason for cancellation
     * @return The result of the transition
     */
    @Transactional
    suspend fun cancelProject(
        projectId: String,
        userId: UserId,
        reason: String
    ): TransitionResult {
        return transitionProject(
            projectId = projectId,
            targetStatus = ProjectStatus.CANCELLED,
            userId = userId,
            comment = "Project cancelled: $reason"
        )
    }
}