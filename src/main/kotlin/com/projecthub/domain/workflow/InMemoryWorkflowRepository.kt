package com.projecthub.domain.workflow

import com.projecthub.domain.project.workflow.config.ProjectWorkflowConfiguration
import com.projecthub.domain.workflow.exception.InvalidWorkflowConfigurationException
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

/**
 * In-memory implementation of the WorkflowRepository interface.
 * This repository stores and provides access to workflow definitions
 * and transitions.
 */
@Repository
class InMemoryWorkflowRepository(
    private val projectTransitions: List<Transition>
) : WorkflowRepository {

    private val logger = LoggerFactory.getLogger(InMemoryWorkflowRepository::class.java)

    // Map of entityType -> Map of fromState -> Map of toState -> Transition
    private val transitionMap = mutableMapOf<String, MutableMap<String, MutableMap<String, Transition>>>()

    // Map of entityType -> Set of valid states
    private val statesMap = mutableMapOf<String, MutableSet<String>>()

    // Map of entityType -> initial state
    private val initialStatesMap = mutableMapOf<String, String>()

    // Map of entityType -> Set of final states
    private val finalStatesMap = mutableMapOf<String, MutableSet<String>>()

    @PostConstruct
    fun init() {
        logger.info("Initializing workflow repository with {} project transitions", projectTransitions.size)

        // Initialize project workflow
        initialStatesMap[ProjectWorkflowConfiguration.PROJECT_ENTITY_TYPE] = "DRAFT"
        finalStatesMap[ProjectWorkflowConfiguration.PROJECT_ENTITY_TYPE] = mutableSetOf("COMPLETED", "CANCELLED")

        // Register all transitions
        projectTransitions.forEach { transition ->
            registerTransition(transition)
        }

        validateWorkflowConfigurations()

        logger.info("Workflow repository initialized with {} entity types", transitionMap.size)
        transitionMap.forEach { (entityType, transitions) ->
            logger.info(
                "Entity type '{}' has {} states and {} transitions",
                entityType, statesMap[entityType]?.size ?: 0,
                transitions.values.sumOf { it.size })
        }
    }

    override suspend fun findTransition(
        entityType: String,
        fromState: String,
        toState: String
    ): Transition? {
        return transitionMap[entityType]?.get(fromState)?.get(toState)
    }

    override suspend fun findAvailableTargetStates(
        entityType: String,
        fromState: String
    ): List<String> {
        return transitionMap[entityType]?.get(fromState)?.keys?.toList() ?: emptyList()
    }

    override suspend fun findStates(entityType: String): List<String> {
        return statesMap[entityType]?.toList() ?: emptyList()
    }

    override suspend fun getInitialState(entityType: String): String {
        return initialStatesMap[entityType] ?: throw IllegalArgumentException(
            "No initial state defined for entity type: $entityType"
        )
    }

    override suspend fun isFinalState(entityType: String, state: String): Boolean {
        return finalStatesMap[entityType]?.contains(state) ?: false
    }

    private fun registerTransition(transition: Transition) {
        // Ensure maps are initialized for this entity type
        val entityTransitions = transitionMap.getOrPut(transition.entityType) { mutableMapOf() }
        val fromStateTransitions = entityTransitions.getOrPut(transition.fromState) { mutableMapOf() }

        // Register the transition
        fromStateTransitions[transition.toState] = transition

        // Track states
        val entityStates = statesMap.getOrPut(transition.entityType) { mutableSetOf() }
        entityStates.add(transition.fromState)
        entityStates.add(transition.toState)
    }

    private fun validateWorkflowConfigurations() {
        // Validate each entity type has an initial state
        for (entityType in transitionMap.keys) {
            if (!initialStatesMap.containsKey(entityType)) {
                throw InvalidWorkflowConfigurationException(
                    "No initial state defined for entity type: $entityType"
                )
            }

            // Validate initial state is a valid state
            val initialState = initialStatesMap[entityType]
            if (!statesMap[entityType]!!.contains(initialState)) {
                throw InvalidWorkflowConfigurationException(
                    "Initial state '$initialState' is not a valid state for entity type: $entityType"
                )
            }

            // Validate each final state is a valid state
            for (finalState in finalStatesMap[entityType] ?: emptySet()) {
                if (!statesMap[entityType]!!.contains(finalState)) {
                    throw InvalidWorkflowConfigurationException(
                        "Final state '$finalState' is not a valid state for entity type: $entityType"
                    )
                }
            }
        }
    }
}
