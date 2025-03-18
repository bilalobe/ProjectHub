package com.projecthub.compose.platform

import com.projecthub.compose.network.HttpClientProvider
import com.projecthub.compose.repository.KtorRepositoryFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * Helper class to initialize repositories based on the platform.
 * This provides platform-specific configurations and lifecycle management.
 */
object PlatformRepositoryInitializer {
    // Coroutine scope for repository operations
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    // Desktop platform initialization
    fun initializeDesktop(baseUrl: String = "http://localhost:8080"): KtorRepositoryFactory.RepositoryContainer {
        return KtorRepositoryFactory.createRepositories(
            baseUrl = baseUrl,
            platform = HttpClientProvider.Platform.DESKTOP
        )
    }
    
    // Mobile (Android) platform initialization
    fun initializeAndroid(baseUrl: String = "http://10.0.2.2:8080"): KtorRepositoryFactory.RepositoryContainer {
        return KtorRepositoryFactory.createRepositories(
            baseUrl = baseUrl,
            platform = HttpClientProvider.Platform.ANDROID
        )
    }
    
    // Web platform initialization
    fun initializeWeb(baseUrl: String = ""): KtorRepositoryFactory.RepositoryContainer {
        // For web, we typically use relative URLs since the API is served from the same origin
        return KtorRepositoryFactory.createRepositories(
            baseUrl = baseUrl,
            platform = HttpClientProvider.Platform.WEB
        )
    }
    
    // Cleanup resources when application is closing
    fun shutdown() {
        HttpClientProvider.release()
        repositoryScope.cancel()
    }
}