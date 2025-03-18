package com.projecthub.application.project.port.`in`

import com.projecthub.application.project.dto.ProjectDTO
import com.projecthub.application.project.dto.CreateProjectCommand
import com.projecthub.application.project.dto.UpdateProjectCommand

/**
 * Inbound port for project management functionality
 * This interface defines the use cases available for managing projects
 */
interface ProjectManagementUseCase {
    /**
     * Creates a new project
     */
    fun createProject(command: CreateProjectCommand): ProjectDTO

    /**
     * Updates an existing project
     */
    fun updateProject(projectId: String, command: UpdateProjectCommand): ProjectDTO

    /**
     * Assigns a project to a team
     */
    fun assignProjectToTeam(projectId: String, teamId: String): ProjectDTO

    /**
     * Completes a project
     */
    fun completeProject(projectId: String): ProjectDTO

    /**
     * Gets a project by ID
     */
    fun getProjectById(projectId: String): ProjectDTO?

    /**
     * Gets all projects
     */
    fun getAllProjects(): List<ProjectDTO>

    /**
     * Gets projects by owner ID
     */
    fun getProjectsByOwner(ownerId: String): List<ProjectDTO>
}
