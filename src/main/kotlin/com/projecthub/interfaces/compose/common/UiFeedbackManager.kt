package com.projecthub.interfaces.ui.common

import androidx.compose.material.SnackbarDuration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Central manager for UI feedback across the application.
 * Handles displaying snackbar messages, dialogs, and loading indicators.
 */
class UiFeedbackManager {
    // Snackbar messages
    private val _snackbarMessage = MutableSharedFlow<SnackbarMessage>()
    val snackbarMessage: SharedFlow<SnackbarMessage> = _snackbarMessage.asSharedFlow()
    
    // Dialog state
    private val _dialogState = MutableStateFlow<DialogState>(DialogState.Hidden)
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()
    
    // Global loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    /**
     * Show a snackbar message
     */
    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        _snackbarMessage.emit(SnackbarMessage(message, actionLabel, duration))
    }
    
    /**
     * Show an error snackbar message
     */
    suspend fun showErrorSnackbar(
        message: String,
        actionLabel: String? = "Dismiss",
        duration: SnackbarDuration = SnackbarDuration.Long
    ) {
        _snackbarMessage.emit(SnackbarMessage(message, actionLabel, duration, isError = true))
    }
    
    /**
     * Show a confirmation dialog
     */
    fun showConfirmationDialog(
        title: String,
        message: String,
        confirmText: String = "Confirm",
        dismissText: String = "Cancel",
        onConfirm: () -> Unit,
        onDismiss: () -> Unit = {}
    ) {
        _dialogState.value = DialogState.Confirmation(
            title = title,
            message = message,
            confirmText = confirmText,
            dismissText = dismissText,
            onConfirm = onConfirm,
            onDismiss = onDismiss
        )
    }
    
    /**
     * Show an error dialog
     */
    fun showErrorDialog(
        title: String = "Error",
        message: String,
        dismissText: String = "OK",
        onDismiss: () -> Unit = {}
    ) {
        _dialogState.value = DialogState.Error(
            title = title,
            message = message,
            dismissText = dismissText,
            onDismiss = onDismiss
        )
    }
    
    /**
     * Hide any currently shown dialog
     */
    fun hideDialog() {
        _dialogState.value = DialogState.Hidden
    }
    
    /**
     * Set the global loading state
     */
    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}

/**
 * Represents a snackbar message to be shown
 */
data class SnackbarMessage(
    val message: String,
    val actionLabel: String? = null,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val isError: Boolean = false
)

/**
 * Represents the current dialog state
 */
sealed class DialogState {
    object Hidden : DialogState()
    
    data class Confirmation(
        val title: String,
        val message: String,
        val confirmText: String = "Confirm",
        val dismissText: String = "Cancel",
        val onConfirm: () -> Unit,
        val onDismiss: () -> Unit = {}
    ) : DialogState()
    
    data class Error(
        val title: String = "Error",
        val message: String,
        val dismissText: String = "OK",
        val onDismiss: () -> Unit = {}
    ) : DialogState()
}