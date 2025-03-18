package com.projecthub.compose.platform.samples

import com.projecthub.compose.auth.ComposeAuthProvider
import com.projecthub.compose.repository.ComposeRepositoryProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Example of repository initialization for Web platform
 */
object WebInitialization {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    /**
     * Initializes repositories for Web application
     * 
     * @param baseUrl Base API URL (defaults to empty string for relative URLs)
     * @return ComposeRepositoryProvider configured for Web
     */
    fun initializeRepositories(baseUrl: String = ""): ComposeRepositoryProvider {
        // Get repository provider configured for Web
        val repositoryProvider = ComposeRepositoryProvider.forWeb(baseUrl)
        
        // Attempt to restore authentication state from browser storage
        applicationScope.launch {
            val authProvider = ComposeAuthProvider.getInstance()
            authProvider.restoreAuthState()
            
            setupBrowserSpecificBehavior(repositoryProvider)
        }
        
        return repositoryProvider
    }
    
    /**
     * Configure browser-specific behavior
     */
    private suspend fun setupBrowserSpecificBehavior(repositoryProvider: ComposeRepositoryProvider) {
        try {
            // For web clients, we might want to optimize initial payload
            // by loading data in stages
            
            // First, load critical UI data
            val currentUserProjects = repositoryProvider.projectRepository.getAllProjects()
            println("Loaded ${currentUserProjects.size} projects for initial render")
            
            // Then, defer loading less critical data
            applicationScope.launch {
                // Simulate deferred loading 
                try {
                    repositoryProvider.studentRepository.getAllStudents()
                } catch (e: Exception) {
                    console.error("Failed to load secondary data: ${e.message}")
                }
            }
            
        } catch (e: Exception) {
            console.error("Initial data loading failed: ${e.message}")
        }
    }
    
    /**
     * Shutdown and cleanup resources
     */
    fun shutdown() {
        try {
            ComposeRepositoryProvider.getInstance(
                baseUrl = "",
                platform = ComposeRepositoryProvider.Platform.WEB
            ).release()
        } catch (e: Exception) {
            console.error("Error during shutdown: ${e.message}")
        }
    }
}