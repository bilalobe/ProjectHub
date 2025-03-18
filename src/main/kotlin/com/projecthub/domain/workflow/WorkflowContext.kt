package com.projecthub.domain.workflow

import com.projecthub.domain.user.UserId
import java.time.Instant

/**
 * Context information for a workflow state transition.
 * This class provides all the necessary details about the transition being attempted,
 * including the entity being transitioned, the user initiating the transition,
 * and the source and target states.
 */
data class WorkflowContext(
    /**
     * The ID of the entity being transitioned.
     */
    val entityId: String,

    /**
     * The type of entity being transitioned.
     */
    val entityType: String,

    /**
     * The ID of the user initiating the transition.
     */
    val userId: UserId,

    /**
     * The current state of the entity.
     */
    val fromState: String,

    /**
     * The target state for the transition.
     */
    val toState: String,

    /**
     * The time when the transition was initiated.
     */
    val timestamp: Instant = Instant.now(),

    /**
     * Optional comment providing additional context about the transition.
     */
    val comment: String? = null,

    /**
     * Additional arbitrary attributes that may be relevant for the transition.
     */
    val attributes: Map<String, Any> = mapOf()
)
