package com.projecthub.interfaces.compose.architecture

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Interface for UI states.
 * All ViewModel states should implement this interface.
 */
interface UiState

/**
 * Interface for UI events/effects.
 * Represents one-time events that ViewModels emit to the UI.
 */
interface UiEvent

/**
 * Interface for user intents/actions.
 * Represents actions that can be performed by the user.
 */
interface UiIntent

/**
 * Base ViewModel class following the MVI pattern.
 * Provides unidirectional data flow for state management.
 *
 * @param State The type of state this ViewModel manages
 * @param Intent The type of intents this ViewModel handles
 * @param Event The type of events this ViewModel emits
 */
abstract class MviViewModel<State : UiState, Intent : UiIntent, Event : UiEvent>(initialState: State) {
    
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()
    
    private val _events = MutableStateFlow<Event?>(null)
    val events: StateFlow<Event?> = _events.asStateFlow()
    
    /**
     * Process a user intent and update state or emit events accordingly
     */
    abstract fun processIntent(intent: Intent)
    
    /**
     * Update the current state
     */
    protected fun updateState(update: (State) -> State) {
        _state.update { update(it) }
    }
    
    /**
     * Emit a one-time event
     */
    protected fun emitEvent(event: Event) {
        _events.value = event
        // Reset event after emission to ensure it's only handled once
        _events.value = null
    }
    
    /**
     * Clear the current event
     */
    fun consumeEvent() {
        _events.value = null
    }
}

/**
 * Extension of MviViewModel that integrates security permissions.
 * Allows ViewModels to make permission-based decisions.
 */
abstract class SecureMviViewModel<State : UiState, Intent : UiIntent, Event : UiEvent>(
    initialState: State,
    private val permissionController: com.projecthub.interfaces.compose.security.UiPermissionController
) : MviViewModel<State, Intent, Event>(initialState) {
    
    /**
     * Check if the current user has a specific permission
     */
    protected fun hasPermission(objectName: String, operation: String, objectId: String? = null): Boolean {
        return permissionController.hasPermission(objectName, operation, objectId)
    }
    
    /**
     * Check if the current user has a specific role
     */
    protected fun hasRole(roleName: String): Boolean {
        return permissionController.hasRole(roleName)
    }
    
    /**
     * Get all permissions for the current user
     */
    protected fun getAllPermissions(): List<org.apache.directory.fortress.core.model.Permission> {
        return permissionController.getAllPermissions()
    }
    
    /**
     * Apply permission filtering to a list of items
     *
     * @param items The list of items to filter
     * @param getPermissionCheck A function that returns the permission check parameters for an item
     * @return The filtered list containing only items the user has permission to access
     */
    protected fun <T> filterByPermission(
        items: List<T>,
        getPermissionCheck: (T) -> Triple<String, String, String?>
    ): List<T> {
        return items.filter { item ->
            val (objectName, operation, objectId) = getPermissionCheck(item)
            hasPermission(objectName, operation, objectId)
        }
    }
}