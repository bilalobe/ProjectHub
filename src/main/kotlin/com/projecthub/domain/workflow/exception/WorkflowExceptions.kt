package com.projecthub.domain.workflow.exception

/**
 * Exception thrown when an invalid workflow transition is attempted.
 * This occurs when trying to transition an entity to a state that
 * is not allowed from its current state.
 */
class InvalidTransitionException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * Exception thrown when a workflow transition fails validation.
 * This occurs when the business rules for a transition are not satisfied.
 */
class ValidationFailedException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * Exception thrown when a workflow entity is in an invalid state.
 * This can occur when an entity's state is not recognized by the workflow.
 */
class InvalidWorkflowStateException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * Exception thrown when a workflow operation is not allowed.
 * This can occur due to authorization issues or when the operation
 * violates workflow constraints.
 */
class WorkflowOperationNotAllowedException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * Exception thrown when a workflow configuration is invalid.
 * This occurs when the workflow definition contains errors.
 */
class InvalidWorkflowConfigurationException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)