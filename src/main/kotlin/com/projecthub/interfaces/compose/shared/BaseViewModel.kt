package com.projecthub.ui.shared

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Base class for all ViewModels to provide common functionality
 */
abstract class BaseViewModel {
    
    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    /**
     * Events emitted to the UI for one-time actions like navigation, showing dialogs, etc.
     */
    sealed class UiEvent {
        data class ShowError(val message: String) : UiEvent()
        data class ShowToast(val message: String) : UiEvent()
        data class Navigate(val route: String) : UiEvent()
        object NavigateBack : UiEvent()
    }

    /**
     * Clean up resources when ViewModel is no longer needed
     */
    open fun onCleared() {
        // Override in subclasses to clean up resources
    }
}