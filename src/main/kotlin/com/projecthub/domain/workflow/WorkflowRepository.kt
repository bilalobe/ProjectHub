package com.projecthub.domain.workflow

/**
 * Repository interface for accessing workflow definitions and transitions.
 */
interface WorkflowRepository {

    /**
     * Finds a workflow transition by entity type and state transition.
     *
     * @param entityType The type of entity
     * @param fromState The source state
     * @param toState The target state
     * @return The transition if found, null otherwise
     */
    suspend fun findTransition(
        entityType: String,
        fromState: String,
        toState: String
    ): Transition?

    /**
     * Finds all available target states for an entity type from a given state.
     *
     * @param entityType The type of entity
     * @param fromState The source state
     * @return A list of available target states
     */
    suspend fun findAvailableTargetStates(
        entityType: String,
        fromState: String
    ): List<String>

    /**
     * Finds all valid states for an entity type.
     *
     * @param entityType The type of entity
     * @return A list of valid states
     */
    suspend fun findStates(entityType: String): List<String>

    /**
     * Gets the initial state for an entity type.
     *
     * @param entityType The type of entity
     * @return The initial state
     */
    suspend fun getInitialState(entityType: String): String

    /**
     * Checks if a state is a final state for an entity type.
     *
     * @param entityType The type of entity
     * @param state The state to check
     * @return true if the state is a final state, false otherwise
     */
    suspend fun isFinalState(entityType: String, state: String): Boolean
}