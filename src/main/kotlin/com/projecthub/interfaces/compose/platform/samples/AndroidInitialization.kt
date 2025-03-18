package com.projecthub.compose.platform.samples

import com.projecthub.compose.auth.ComposeAuthProvider
import com.projecthub.compose.repository.ComposeRepositoryProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Example of repository initialization for Android platform
 */
object AndroidInitialization {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    
    /**
     * Initializes repositories for Android application
     * 
     * @param baseUrl Base API URL (defaults to 10.0.2.2 for Android emulator)
     * @return ComposeRepositoryProvider configured for Android
     */
    fun initializeRepositories(baseUrl: String = "http://10.0.2.2:8080"): ComposeRepositoryProvider {
        // Get repository provider configured for Android
        val repositoryProvider = ComposeRepositoryProvider.forAndroid(baseUrl)
        
        // Attempt to restore authentication state in background
        applicationScope.launch {
            val authProvider = ComposeAuthProvider.getInstance()
            authProvider.restoreAuthState()
            
            // Setup offline caching specifically for mobile
            setupOfflineCaching(repositoryProvider)
        }
        
        return repositoryProvider
    }
    
    /**
     * Configure mobile-specific offline caching
     */
    private suspend fun setupOfflineCaching(repositoryProvider: ComposeRepositoryProvider) {
        try {
            // Pre-load essential data for offline use
            val projects = repositoryProvider.projectRepository.getAllProjects()
            
            // Here you would implement platform-specific data caching
            // This is just a placeholder for the implementation
            println("Cached ${projects.size} projects for offline use")
            
        } catch (e: Exception) {
            // Handle network errors and fallback to cached data
            println("Using cached data: ${e.message}")
        }
    }
    
    /**
     * Shutdown and cleanup resources
     */
    fun shutdown() {
        try {
            ComposeRepositoryProvider.getInstance(
                baseUrl = "http://10.0.2.2:8080",
                platform = ComposeRepositoryProvider.Platform.ANDROID
            ).release()
        } catch (e: Exception) {
            println("Error during shutdown: ${e.message}")
        }
    }
}