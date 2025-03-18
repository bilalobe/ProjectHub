package com.projecthub.interfaces.ui.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel class that follows the MVI pattern.
 * Provides a standardized way to handle state updates, events, and side effects.
 *
 * @param S The type of UI state this ViewModel will manage
 * @param E The type of UI events this ViewModel will process
 * @param F The type of UI effects this ViewModel will emit
 * @param initialState The initial state of the UI
 */
abstract class MviViewModel<S : UiState, E : UiEvent, F : UiEffect>(
    initialState: S,
    private val scope: CoroutineScope
) {
    // The current state of the UI
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    // Effect channel for one-time events
    private val _effect = MutableSharedFlow<F>()
    val effect: SharedFlow<F> = _effect.asSharedFlow()

    /**
     * Process a UI event and update state or emit effects accordingly.
     * This is the main entry point for UI components to interact with the ViewModel.
     */
    abstract fun processEvent(event: E)

    /**
     * Update the current state of the UI.
     * Should be called only from within the ViewModel.
     */
    protected fun updateState(reduce: S.() -> S) {
        val newState = _state.value.reduce()
        _state.value = newState
    }

    /**
     * Emit a UI effect (side effect).
     * Should be called only from within the ViewModel.
     */
    protected fun emitEffect(effect: F) {
        scope.launch {
            _effect.emit(effect)
        }
    }
}