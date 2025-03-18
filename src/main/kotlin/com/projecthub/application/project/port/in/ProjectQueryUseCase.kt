package com.projecthub.core.application.project.port.`in`

import java.time.LocalDateTime
import java.util.Optional

import com.projecthub.core.domain.project.model.Project
import com.projecthub.core.domain.project.model.ProjectId
import com.projecthub.core.domain.project.model.ProjectPriority
import com.projecthub.core.domain.project.model.ProjectStatus
import com.projecthub.core.domain.project.model.UserId

/**
 * Input port defining query operations for projects.
 * This separates read operations from write operations, following CQRS principles.
 */
interface ProjectQueryUseCase {

    /**
     * Finds a project by its ID.
     *
     * @param id The project ID
     * @return The project if found, otherwise empty
     */
    fun getProjectById(id: ProjectId): Optional<Project>

    /**
     * Gets all projects.
     *
     * @return A list of all projects
     */
    fun getAllProjects(): List<Project>

    /**
     * Gets projects with a specific status.
     *
     * @param status The status to filter by
     * @return A list of projects with the given status
     */
    fun getProjectsByStatus(status: ProjectStatus): List<Project>

    /**
     * Gets projects owned by a specific user.
     *
     * @param ownerId The ID of the owner
     * @return A list of projects owned by the user
     */
    fun getProjectsByOwner(ownerId: UserId): List<Project>

    /**
     * Gets projects with a specific priority.
     *
     * @param priority The priority to filter by
     * @return A list of projects with the given priority
     */
    fun getProjectsByPriority(priority: ProjectPriority): List<Project>

    /**
     * Gets projects created within a date range.
     *
     * @param from The start date (inclusive)
     * @param to The end date (inclusive)
     * @return A list of projects created within the date range
     */
    fun getProjectsCreatedBetween(from: LocalDateTime, to: LocalDateTime): List<Project>

    /**
     * Searches for projects by name or description containing the search term.
     *
     * @param searchTerm The term to search for
     * @return A list of projects matching the search term
     */
    fun searchProjects(searchTerm: String): List<Project>
}
