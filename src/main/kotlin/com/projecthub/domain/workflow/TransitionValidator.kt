package com.projecthub.domain.workflow

/**
 * Functional interface for validating workflow state transitions.
 * Validators enforce business rules that must be satisfied before
 * an entity can transition from one workflow state to another.
 */
fun interface TransitionValidator {

    /**
     * Validates if a transition is allowed in the given context.
     *
     * @param context The workflow context containing relevant information
     * @return true if the transition is allowed, false otherwise
     */
    suspend fun validate(context: WorkflowContext): Boolean
}
