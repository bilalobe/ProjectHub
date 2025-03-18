package com.projecthub.compose.repository

import com.projecthub.compose.bridge.KtorServiceBridge
import com.projecthub.compose.network.HttpClientProvider
import com.projecthub.ui.project.repository.ProjectRepository
import com.projecthub.ui.student.repository.StudentRepository
import com.projecthub.ui.task.repository.TaskRepository

/**
 * Factory for creating Ktor-based repository implementations.
 * This provides a single point for initializing all repositories with the appropriate
 * service bridge configured for each platform.
 */
object KtorRepositoryFactory {
    /**
     * Creates all repositories needed for a specific platform
     */
    fun createRepositories(
        baseUrl: String,
        platform: HttpClientProvider.Platform
    ): RepositoryContainer {
        val serviceBridge = KtorServiceBridge(baseUrl, platform)
        
        return RepositoryContainer(
            projectRepository = KtorProjectRepository(serviceBridge),
            taskRepository = KtorTaskRepository(serviceBridge),
            // Add additional repositories here as they're implemented
            serviceBridge = serviceBridge
        )
    }
    
    /**
     * Container for all repositories used by the application
     */
    data class RepositoryContainer(
        val projectRepository: ProjectRepository,
        val taskRepository: TaskRepository,
        internal val serviceBridge: KtorServiceBridge
    ) {
        /**
         * Releases all resources used by the repositories
         */
        fun release() {
            serviceBridge.release()
        }
    }
}