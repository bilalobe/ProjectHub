package com.projecthub.compose.repository

import com.projecthub.ui.project.model.ProjectStatus
import com.projecthub.ui.project.repository.ProjectDto
import com.projecthub.ui.project.repository.ProjectRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.first
import java.util.UUID

/**
 * Shared Ktor-based implementation of ProjectRepository that works across all platforms.
 * This implementation uses the platform-specific HTTP client directly.
 */
class KtorProjectRepository(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : ProjectRepository {

    override suspend fun getAllProjects(): List<ProjectDto> {
        val response = httpClient.get("$baseUrl/api/projects")
        return response.body()
    }

    override suspend fun getProjectById(id: UUID): ProjectDto {
        val response = httpClient.get("$baseUrl/api/projects/$id")
        return response.body()
    }

    override suspend fun createProject(name: String, description: String, deadline: String?): ProjectDto {
        val response = httpClient.post("$baseUrl/api/projects") {
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "name" to name,
                "description" to description,
                "deadline" to deadline
            ))
        }
        return response.body()
    }

    override suspend fun updateProject(
        id: UUID,
        name: String,
        description: String,
        status: ProjectStatus,
        deadline: String?
    ): ProjectDto {
        val response = httpClient.put("$baseUrl/api/projects/$id") {
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "name" to name,
                "description" to description,
                "status" to status.name,
                "deadline" to deadline
            ))
        }
        return response.body()
    }

    override suspend fun deleteProject(id: UUID) {
        httpClient.delete("$baseUrl/api/projects/$id")
    }
}