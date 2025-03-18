package com.projecthub.interfaces.ui.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Central navigation coordinator for the entire application.
 * Handles navigation requests from any part of the application.
 */
class NavigationCoordinator {
    // Navigation commands flow
    private val _navigationCommands = MutableSharedFlow<NavigationCommand>()
    val navigationCommands: SharedFlow<NavigationCommand> = _navigationCommands.asSharedFlow()
    
    // Navigation history
    private val history = ArrayDeque<String>()
    
    /**
     * Navigate to the specified route
     */
    suspend fun navigateTo(route: String) {
        history.addLast(route)
        _navigationCommands.emit(NavigationCommand.NavigateTo(route))
    }
    
    /**
     * Navigate to the specified route, clearing the back stack
     */
    suspend fun navigateToWithClear(route: String) {
        history.clear()
        history.addLast(route)
        _navigationCommands.emit(NavigationCommand.NavigateToWithClear(route))
    }
    
    /**
     * Navigate back in the application
     */
    suspend fun navigateBack() {
        if (history.size > 1) {
            history.removeLast()
            _navigationCommands.emit(NavigationCommand.NavigateBack)
        }
    }
    
    /**
     * Navigate up in the application hierarchy
     */
    suspend fun navigateUp() {
        _navigationCommands.emit(NavigationCommand.NavigateUp)
    }
}

/**
 * Navigation commands that can be processed by the application's NavHost
 */
sealed class NavigationCommand {
    data class NavigateTo(val route: String) : NavigationCommand()
    data class NavigateToWithClear(val route: String) : NavigationCommand()
    object NavigateBack : NavigationCommand()
    object NavigateUp : NavigationCommand()
}