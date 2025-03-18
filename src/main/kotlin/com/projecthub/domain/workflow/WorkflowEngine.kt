package com.projecthub.domain.workflow

import com.projecthub.domain.DomainService
import org.springframework.stereotype.Service
import com.projecthub.domain.workflow.port.out.WorkflowEventPort

/**
 * Core domain service responsible for managing workflow state transitions.
 * This service enforces business rules around workflow transitions, ensuring
 * that only valid state changes are permitted and executing required actions.
 *
 * As a pure domain service, it depends only on domain concepts and output ports.
 */
@Service
class WorkflowEngine(
    private val workflowEventPort: WorkflowEventPort
) : DomainService {

    /**
     * Attempts to transition an entity to a new state using the provided workflow definition.
     * Validates the transition is allowed, executes transition actions, and publishes events.
     *
     * @param workflow The workflow definition containing allowed transitions
     * @param context The context containing current state, target state and entity information
     * @throws IllegalStateException if the transition is not allowed or validation fails
     */
    fun transition(workflow: WorkflowDefinition, context: WorkflowContext) {
        val currentState = context.currentState
        val targetState = context.targetState

        // Verify the transition is allowed by the workflow definition
        if (!workflow.isTransitionAllowed(currentState, targetState)) {
            throw IllegalStateException(
                "Transition from ${currentState.name} to ${targetState.name} is not allowed in workflow ${workflow.name}"
            )
        }

        // Find the transition
        val transition = workflow.getAvailableTransitions(currentState)
            .find { it.toState == targetState }
            ?: throw IllegalStateException("Transition not found")

        // Validate the transition
        if (!transition.isValid(context)) {
            throw IllegalStateException("Transition validation failed")
        }

        // Execute transition actions
        transition.executeActions(context)

        // Publish transition event through output port
        publishTransitionEvent(workflow, context, transition)
    }

    private fun publishTransitionEvent(
        workflow: WorkflowDefinition,
        context: WorkflowContext,
        transition: WorkflowTransition
    ) {
        val event = WorkflowTransitionEvent(
            workflowId = workflow.id,
            entityId = context.entityId,
            entityType = context.entityType,
            fromState = transition.fromState,
            toState = transition.toState,
            transitionName = transition.name
        )

        workflowEventPort.publishTransitionEvent(event)
    }
}

/**
 * Event published when a workflow transition occurs
 */
data class WorkflowTransitionEvent(
    val workflowId: String,
    val entityId: String,
    val entityType: String,
    val fromState: WorkflowState,
    val toState: WorkflowState,
    val transitionName: String
)
