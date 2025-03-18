package com.projecthub.application.project.port.api

import com.projecthub.application.project.domain.Project
import com.projecthub.application.project.domain.ProjectStatus
import java.time.LocalDateTime

/**
 * Service interface for project domain operations
 * Defines the API contract for the Project domain
 */
interface ProjectService {
    fun createProject(request: CreateProjectRequest): ProjectDto
    fun getProject(id: String): ProjectDto?
    fun updateProject(id: String, request: UpdateProjectRequest): ProjectDto?
    fun changeStatus(id: String, status: ProjectStatus): ProjectDto?
    fun assignTeam(id: String, teamId: String): ProjectDto?
    fun listProjects(filter: ProjectFilter): List<ProjectDto>
    fun deleteProject(id: String)
}

data class CreateProjectRequest(
    val name: String,
    val description: String,
    val ownerId: String,
    val teamId: String
)

data class UpdateProjectRequest(
    val name: String? = null,
    val description: String? = null,
    val completionDate: LocalDateTime? = null
)

data class ProjectFilter(
    val ownerId: String? = null,
    val teamId: String? = null,
    val status: ProjectStatus? = null
)

data class ProjectDto(
    val id: String,
    val name: String,
    val description: String,
    val ownerId: String,
    val teamId: String,
    val status: ProjectStatus,
    val completionDate: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
