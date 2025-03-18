package com.projecthub.compose.mvi

import com.projecthub.compose.viewmodel.BaseViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Base implementation of an MVI ViewModel that handles state management
 * and side effects.
 */
abstract class BaseMviViewModel<S : MviState, I : MviIntent, E : MviEffect>(
    initialState: S
) : BaseViewModel(), MviModel<S, I, E> {

    private val _state = MutableStateFlow(initialState)
    override val state: S get() = _state.value
    
    // Public immutable state flow for the UI
    val stateFlow: StateFlow<S> = _state.asStateFlow()
    
    // Channel for side effects
    private val effectChannel = Channel<E>(Channel.BUFFERED)
    
    override suspend fun collectEffects(collector: suspend (E) -> Unit) {
        effectChannel.receiveAsFlow().collect(collector)
    }
    
    /**
     * Update the current state using a reducer function
     */
    protected fun updateState(reducer: S.() -> S) {
        val newState = state.reducer()
        _state.value = newState
    }
    
    /**
     * Emit a side effect
     */
    protected fun emitEffect(effect: E) {
        viewModelScope.launch {
            effectChannel.send(effect)
        }
    }
    
    /**
     * Handle state updates based on the current state and an intent
     */
    protected abstract fun handleIntent(intent: I, currentState: S)
    
    override fun processIntent(intent: I) {
        handleIntent(intent, state)
    }
}