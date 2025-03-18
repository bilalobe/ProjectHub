package com.projecthub.domain.workflow

import com.projecthub.domain.security.audit.SecurityAuditService
import com.projecthub.domain.security.audit.SecurityEventType
import com.projecthub.domain.security.aspect.Auditable
import com.projecthub.domain.workflow.exception.InvalidTransitionException
import com.projecthub.domain.workflow.exception.ValidationFailedException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service for managing workflow state transitions for domain entities.
 * This service coordinates validation and execution of workflow transitions.
 */
@Service
class WorkflowService(
    private val workflowRepository: WorkflowRepository,
    private val securityAuditService: SecurityAuditService
) {
    private val logger = LoggerFactory.getLogger(WorkflowService::class.java)

    /**
     * Transitions an entity from one state to another, validating the transition
     * and executing any associated actions.
     *
     * @param context The workflow context containing transition details
     * @param skipValidation Whether to skip transition validation (use with caution)
     * @return The result of the transition
     * @throws InvalidTransitionException if the transition is not allowed
     * @throws ValidationFailedException if the transition fails validation
     */
    @Transactional
    @Auditable(type = SecurityEventType.SYSTEM_CONFIGURATION, message = "Workflow state transition")
    suspend fun transition(
        context: WorkflowContext,
        skipValidation: Boolean = false
    ): TransitionResult {
        logger.info(
            "Processing transition request: {} -> {} for entity {} ({})",
            context.fromState,
            context.toState,
            context.entityId,
            context.entityType
        )

        // Check if transition is defined in workflow
        val transition = workflowRepository.findTransition(
            entityType = context.entityType,
            fromState = context.fromState,
            toState = context.toState
        ) ?: throw InvalidTransitionException(
            "Transition from ${context.fromState} to ${context.toState} is not allowed for ${context.entityType}"
        )

        // Skip validation if requested, or validate the transition
        if (!skipValidation) {
            val validationResult = validateTransition(transition, context)
            if (!validationResult.isValid) {
                throw ValidationFailedException(
                    "Transition validation failed: ${validationResult.errorMessages.joinToString(", ")}"
                )
            }
        }

        // Execute actions associated with the transition
        val actionResults = executeActions(transition, context)

        val result = TransitionResult(
            entityId = context.entityId,
            fromState = context.fromState,
            toState = context.toState,
            timestamp = context.timestamp,
            actionResults = actionResults
        )

        logger.info(
            "Transition completed: {} -> {} for entity {} ({})",
            context.fromState,
            context.toState,
            context.entityId,
            context.entityType
        )

        return result
    }

    /**
     * Validates a transition using all associated validators.
     */
    private suspend fun validateTransition(
        transition: Transition,
        context: WorkflowContext
    ): ValidationResult {
        logger.debug("Validating transition with {} validators", transition.validators.size)

        val validationBuilder = ValidationResult.Builder()

        for (validator in transition.validators) {
            try {
                if (!validator.validate(context)) {
                    validationBuilder.addError("Validation failed: $validator")
                }
            } catch (e: Exception) {
                logger.error("Error during transition validation", e)
                validationBuilder.addError("Validation error: ${e.message}")
            }
        }

        return validationBuilder.build()
    }

    /**
     * Executes all actions associated with a transition.
     */
    private suspend fun executeActions(
        transition: Transition,
        context: WorkflowContext
    ): List<ActionResult> {
        logger.debug("Executing {} actions for transition", transition.actions.size)

        val results = mutableListOf<ActionResult>()

        for (action in transition.actions) {
            try {
                action.execute(context)
                results.add(ActionResult(success = true))
            } catch (e: Exception) {
                logger.error("Error executing transition action", e)
                results.add(ActionResult(success = false, errorMessage = e.message))
            }
        }

        return results
    }

    /**
     * Gets all available target states for an entity from its current state.
     */
    suspend fun getAvailableTransitions(entityType: String, currentState: String): Flow<String> {
        return workflowRepository.findAvailableTargetStates(entityType, currentState)
            .asFlow()
            .filter { targetState ->
                val transition = workflowRepository.findTransition(
                    entityType = entityType,
                    fromState = currentState,
                    toState = targetState
                )
                transition != null
            }
    }
}

/**
 * Represents the result of a workflow transition.
 */
data class TransitionResult(
    val entityId: String,
    val fromState: String,
    val toState: String,
    val timestamp: java.time.Instant,
    val actionResults: List<ActionResult>
)

/**
 * Represents the result of executing a transition action.
 */
data class ActionResult(
    val success: Boolean,
    val errorMessage: String? = null
)

/**
 * Represents a validation result.
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessages: List<String>
) {
    class Builder {
        private val errors = mutableListOf<String>()

        fun addError(message: String): Builder {
            errors.add(message)
            return this
        }

        fun build(): ValidationResult {
            return ValidationResult(
                isValid = errors.isEmpty(),
                errorMessages = errors.toList()
            )
        }
    }
}

/**
 * Represents a workflow transition.
 */
data class Transition(
    val fromState: String,
    val toState: String,
    val entityType: String,
    val validators: List<TransitionValidator>,
    val actions: List<TransitionAction>
)
