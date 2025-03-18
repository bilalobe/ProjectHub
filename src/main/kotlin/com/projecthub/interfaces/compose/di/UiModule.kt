package com.projecthub.interfaces.ui.di

import com.projecthub.interfaces.ui.auth.AuthService
import com.projecthub.interfaces.ui.auth.TokenManager
import com.projecthub.interfaces.ui.common.UiFeedbackManager
import com.projecthub.interfaces.ui.navigation.NavigationCoordinator
import com.projecthub.interfaces.ui.network.ApiClient
import com.projecthub.interfaces.ui.network.KtorApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Dependency Injection module for UI components.
 * Provides a central place to create and manage all shared UI dependencies.
 * This can be adapted to work with DI frameworks like Koin, Dagger, or Hilt.
 */
object UiModule {
    // Configuration
    private const val API_BASE_URL = "https://api.projecthub.com"
    
    // Application scope for background tasks that should survive configuration changes
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    // Singleton instances
    private val tokenManager by lazy { TokenManager() }
    
    private val apiClient by lazy { 
        KtorApiClient(
            baseUrl = API_BASE_URL,
            tokenProvider = { tokenManager.getAccessToken() }
        )
    }
    
    private val navigationCoordinator by lazy { NavigationCoordinator() }
    
    private val uiFeedbackManager by lazy { UiFeedbackManager() }
    
    private val authService by lazy { AuthService(apiClient, tokenManager) }
    
    /**
     * Get the application-wide coroutine scope for background tasks
     */
    fun provideApplicationScope(): CoroutineScope = applicationScope
    
    /**
     * Get the API client for making network requests
     */
    fun provideApiClient(): ApiClient = apiClient
    
    /**
     * Get the token manager for authentication state
     */
    fun provideTokenManager(): TokenManager = tokenManager
    
    /**
     * Get the navigation coordinator for app navigation
     */
    fun provideNavigationCoordinator(): NavigationCoordinator = navigationCoordinator
    
    /**
     * Get the UI feedback manager for showing messages and dialogs
     */
    fun provideUiFeedbackManager(): UiFeedbackManager = uiFeedbackManager
    
    /**
     * Get the authentication service
     */
    fun provideAuthService(): AuthService = authService
    
    /**
     * Create a ViewModelScope for a specific screen or component
     */
    fun provideViewModelScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
}