package com.projecthub.domain.project.workflow.validator

import com.projecthub.domain.project.Project
import com.projecthub.domain.project.ProjectRepository
import com.projecthub.domain.workflow.TransitionValidator
import com.projecthub.domain.workflow.WorkflowContext
import org.springframework.stereotype.Component

/**
 * Provides validators for project workflow transitions.
 * These validators enforce business rules that must be satisfied
 * before a project can transition from one state to another.
 */
@Component
class ProjectWorkflowValidators(
    private val projectRepository: ProjectRepository
) {

    /**
     * Validates that a project has a team assigned.
     * Projects must have a team before they can transition to certain states.
     */
    fun hasTeamAssigned(): TransitionValidator = TransitionValidator { context ->
        getProject(context).teamId != null
    }

    /**
     * Validates that a project has at least one task defined.
     * Projects must have tasks before they can transition to active state.
     */
    fun hasMinimumTasks(): TransitionValidator = TransitionValidator { context ->
        val project = getProject(context)
        project.tasks.isNotEmpty()
    }

    /**
     * Validates that a project has a start date set.
     * Projects must have a defined start date before they can be activated.
     */
    fun isProjectStartDateSet(): TransitionValidator = TransitionValidator { context ->
        getProject(context).startDate != null
    }

    /**
     * Validates that a project can be canceled.
     * Only projects that are not completed can be canceled.
     */
    fun canBeCancelled(): TransitionValidator = TransitionValidator { context ->
        !getProject(context).isCompleted
    }

    /**
     * Validates that a project can be completed.
     * Projects must have all tasks completed and all deliverables submitted.
     */
    fun canBeCompleted(): TransitionValidator = TransitionValidator { context ->
        val project = getProject(context)
        project.tasks.isNotEmpty() &&
            project.tasks.all { it.isCompleted } &&
            project.milestones.all { it.isCompleted }
    }

    /**
     * Validates that a project is ready for review.
     */
    fun isReadyForReview(): TransitionValidator = TransitionValidator { context ->
        val project = getProject(context)
        project.tasks.isNotEmpty() &&
            project.tasks.count { it.isCompleted } >= (project.tasks.size * 0.8)
    }

    /**
     * Gets the project from the workflow context.
     */
    private suspend fun getProject(context: WorkflowContext): Project {
        return projectRepository.findById(context.entityId)
            ?: throw IllegalStateException("Project not found with ID: ${context.entityId}")
    }
}
