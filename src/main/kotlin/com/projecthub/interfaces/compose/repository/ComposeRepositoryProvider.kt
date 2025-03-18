package com.projecthub.compose.repository

import com.projecthub.compose.auth.ComposeAuthProvider
import com.projecthub.compose.network.PlatformHttpClient
import com.projecthub.ui.project.repository.ProjectRepository
import com.projecthub.ui.task.repository.TaskRepository
import com.projecthub.ui.student.repository.StudentRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Centralized repository provider that manages platform-specific repository instances
 * This class serves as the entry point for UI components to access data repositories.
 */
class ComposeRepositoryProvider private constructor(
    private val baseUrl: String,
    private val platform: Platform,
    private val logLevel: LogLevel = LogLevel.INFO
) {
    // Shared coroutine scope for all repositories
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Authentication provider
    private val authProvider = ComposeAuthProvider.getInstance()
    
    // Platform-specific HTTP client
    private val httpClient: HttpClient by lazy {
        when (platform) {
            Platform.DESKTOP -> PlatformHttpClient.createDesktopClient { authProvider.getAuthToken() }
            Platform.ANDROID -> PlatformHttpClient.createAndroidClient { authProvider.getAuthToken() }
            Platform.WEB -> PlatformHttpClient.createWebClient { authProvider.getAuthToken() }
        }
    }
    
    // Repository instances using the HTTP client
    val projectRepository: ProjectRepository by lazy {
        KtorProjectRepository(httpClient, baseUrl)
    }
    
    val taskRepository: TaskRepository by lazy {
        KtorTaskRepository(httpClient, baseUrl)
    }
    
    val studentRepository: StudentRepository by lazy {
        KtorStudentRepository(httpClient, baseUrl)
    }
    
    /**
     * Release resources when no longer needed
     */
    fun release() {
        httpClient.close()
    }
    
    /**
     * Platform types for configuration
     */
    enum class Platform {
        DESKTOP, ANDROID, WEB
    }
    
    companion object {
        // Singleton instance
        @Volatile
        private var INSTANCE: ComposeRepositoryProvider? = null
        
        /**
         * Get the repository provider instance configured for the current platform
         * 
         * @param baseUrl Base URL for API requests
         * @param platform Current platform type
         * @param logLevel Logging level for HTTP requests
         * @return ComposeRepositoryProvider instance
         */
        fun getInstance(
            baseUrl: String,
            platform: Platform,
            logLevel: LogLevel = LogLevel.INFO
        ): ComposeRepositoryProvider {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ComposeRepositoryProvider(baseUrl, platform, logLevel).also {
                    INSTANCE = it
                }
            }
        }
        
        /**
         * Initialize desktop repositories with default configuration
         * 
         * @param baseUrl Base URL for API requests (defaults to localhost)
         * @return ComposeRepositoryProvider instance for desktop platform
         */
        fun forDesktop(baseUrl: String = "http://localhost:8080"): ComposeRepositoryProvider {
            return getInstance(baseUrl, Platform.DESKTOP)
        }
        
        /**
         * Initialize Android repositories with default configuration
         * 
         * @param baseUrl Base URL for API requests (defaults to 10.0.2.2 for emulator)
         * @return ComposeRepositoryProvider instance for Android platform
         */
        fun forAndroid(baseUrl: String = "http://10.0.2.2:8080"): ComposeRepositoryProvider {
            return getInstance(baseUrl, Platform.ANDROID)
        }
        
        /**
         * Initialize Web repositories with default configuration
         * 
         * @param baseUrl Base URL for API requests (defaults to empty for relative paths)
         * @return ComposeRepositoryProvider instance for Web platform
         */
        fun forWeb(baseUrl: String = ""): ComposeRepositoryProvider {
            return getInstance(baseUrl, Platform.WEB)
        }
    }
}