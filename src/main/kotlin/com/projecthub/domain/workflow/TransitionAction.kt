package com.projecthub.domain.workflow

/**
 * Functional interface for defining actions that execute when a workflow
 * state transition occurs.
 *
 * Actions implement business logic that should be performed when an entity
 * transitions from one state to another in a workflow.
 */
fun interface TransitionAction {

    /**
     * Executes the action in the given workflow context.
     *
     * @param context The workflow context containing relevant information about the transition
     */
    suspend fun execute(context: WorkflowContext)
}
