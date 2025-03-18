package com.projecthub.compose.mvi

/**
 * Base interface for all MVI states in the application.
 * Every screen's state should implement this interface.
 */
interface MviState

/**
 * Base interface for all MVI intents/actions in the application.
 * Every screen's intents should implement this interface.
 */
interface MviIntent

/**
 * Base interface for all MVI effects (side effects) in the application.
 * Effects are one-time events that should trigger UI actions like showing
 * a snackbar or navigating to another screen.
 */
interface MviEffect

/**
 * Base interface for all MVI models (ViewModels) in the application.
 * 
 * @param S The type of State this model manages
 * @param I The type of Intent this model handles
 * @param E The type of Effect this model can produce
 */
interface MviModel<S : MviState, I : MviIntent, E : MviEffect> {
    /**
     * The current UI state
     */
    val state: S

    /**
     * Process a new intent/action
     */
    fun processIntent(intent: I)

    /**
     * Collect effects from this model
     */
    suspend fun collectEffects(collector: suspend (E) -> Unit)
}