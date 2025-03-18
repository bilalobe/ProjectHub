package com.projecthub.compose.platform.samples

import com.projecthub.compose.auth.ComposeAuthProvider
import com.projecthub.compose.repository.ComposeRepositoryProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Example of repository initialization for Desktop platform
 */
object DesktopInitialization {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    /**
     * Initializes repositories for desktop application
     * 
     * @param baseUrl Base API URL (defaults to localhost)
     * @return ComposeRepositoryProvider configured for desktop
     */
    fun initializeRepositories(baseUrl: String = "http://localhost:8080"): ComposeRepositoryProvider {
        // Get repository provider configured for desktop
        val repositoryProvider = ComposeRepositoryProvider.forDesktop(baseUrl)
        
        // Attempt to restore authentication state in background
        applicationScope.launch {
            val authProvider = ComposeAuthProvider.getInstance()
            authProvider.restoreAuthState()
            
            // Example of loading initial data
            try {
                // Pre-load some common data
                repositoryProvider.projectRepository.getAllProjects()
            } catch (e: Exception) {
                // Handle potential network errors
                println("Failed to pre-load data: ${e.message}")
            }
        }
        
        return repositoryProvider
    }
    
    /**
     * Shutdown and cleanup resources
     */
    fun shutdown() {
        // Release repository resources
        try {
            ComposeRepositoryProvider.getInstance(
                baseUrl = "http://localhost:8080",
                platform = ComposeRepositoryProvider.Platform.DESKTOP
            ).release()
        } catch (e: Exception) {
            // Handle cleanup exceptions
            println("Error during shutdown: ${e.message}")
        }
    }
}