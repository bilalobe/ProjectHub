package com.projecthub.domain.project.workflow.config

import com.projecthub.domain.project.ProjectStatus
import com.projecthub.domain.project.workflow.action.ProjectWorkflowActions
import com.projecthub.domain.project.workflow.validator.ProjectWorkflowValidators
import com.projecthub.domain.workflow.Transition
import com.projecthub.domain.workflow.TransitionAction
import com.projecthub.domain.workflow.TransitionValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for the project workflow.
 * Defines all valid state transitions and associates validators and actions with each transition.
 */
@Configuration
class ProjectWorkflowConfiguration(
    private val projectWorkflowValidators: ProjectWorkflowValidators,
    private val projectWorkflowActions: ProjectWorkflowActions
) {
    companion object {
        const val PROJECT_ENTITY_TYPE = "project"
    }

    /**
     * Defines all possible project transitions with their validators and actions.
     */
    @Bean("projectTransitions")
    fun projectTransitions(): List<Transition> {
        return listOf(
            // DRAFT -> PLANNING transition
            createTransition(
                fromState = ProjectStatus.DRAFT.name,
                toState = ProjectStatus.PLANNING.name,
                validators = listOf(
                    projectWorkflowValidators.hasTeamAssigned()
                ),
                actions = listOf(
                    projectWorkflowActions.updateLastActivity(),
                    projectWorkflowActions.notifyProjectStatusChanged()
                )
            ),

            // PLANNING -> IN_PROGRESS transition
            createTransition(
                fromState = ProjectStatus.PLANNING.name,
                toState = ProjectStatus.IN_PROGRESS.name,
                validators = listOf(
                    projectWorkflowValidators.hasTeamAssigned(),
                    projectWorkflowValidators.hasMinimumTasks(),
                    projectWorkflowValidators.isProjectStartDateSet()
                ),
                actions = listOf(
                    projectWorkflowActions.updateStartDate(),
                    projectWorkflowActions.assignUnassignedTasks(),
                    projectWorkflowActions.updateLastActivity(),
                    projectWorkflowActions.notifyProjectStatusChanged()
                )
            ),

            // IN_PROGRESS -> REVIEW transition
            createTransition(
                fromState = ProjectStatus.IN_PROGRESS.name,
                toState = ProjectStatus.REVIEW.name,
                validators = listOf(
                    projectWorkflowValidators.isReadyForReview()
                ),
                actions = listOf(
                    projectWorkflowActions.updateLastActivity(),
                    projectWorkflowActions.notifyProjectStatusChanged()
                )
            ),

            // REVIEW -> COMPLETED transition
            createTransition(
                fromState = ProjectStatus.REVIEW.name,
                toState = ProjectStatus.COMPLETED.name,
                validators = listOf(
                    projectWorkflowValidators.canBeCompleted()
                ),
                actions = listOf(
                    projectWorkflowActions.updateCompletionDate(),
                    projectWorkflowActions.updateLastActivity(),
                    projectWorkflowActions.notifyProjectStatusChanged()
                )
            ),

            // Any state -> CANCELLED transition
            createTransition(
                fromState = ProjectStatus.DRAFT.name,
                toState = ProjectStatus.CANCELLED.name,
                validators = listOf(
                    projectWorkflowValidators.canBeCancelled()
                ),
                actions = listOf(
                    projectWorkflowActions.updateLastActivity(),
                    projectWorkflowActions.notifyProjectStatusChanged()
                )
            ),
            createTransition(
                fromState = ProjectStatus.PLANNING.name,
                toState = ProjectStatus.CANCELLED.name,
                validators = listOf(
                    projectWorkflowValidators.canBeCancelled()
                ),
                actions = listOf(
                    projectWorkflowActions.updateLastActivity(),
                    projectWorkflowActions.notifyProjectStatusChanged()
                )
            ),
            createTransition(
                fromState = ProjectStatus.IN_PROGRESS.name,
                toState = ProjectStatus.CANCELLED.name,
                validators = listOf(
                    projectWorkflowValidators.canBeCancelled()
                ),
                actions = listOf(
                    projectWorkflowActions.updateLastActivity(),
                    projectWorkflowActions.notifyProjectStatusChanged()
                )
            ),

            // REVIEW -> IN_PROGRESS (return to development)
            createTransition(
                fromState = ProjectStatus.REVIEW.name,
                toState = ProjectStatus.IN_PROGRESS.name,
                validators = emptyList(),
                actions = listOf(
                    projectWorkflowActions.updateLastActivity(),
                    projectWorkflowActions.notifyProjectStatusChanged()
                )
            )
        )
    }

    private fun createTransition(
        fromState: String,
        toState: String,
        validators: List<TransitionValidator>,
        actions: List<TransitionAction>
    ): Transition {
        return Transition(
            fromState = fromState,
            toState = toState,
            entityType = PROJECT_ENTITY_TYPE,
            validators = validators,
            actions = actions
        )
    }
}
