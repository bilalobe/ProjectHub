package com.projecthub.infrastructure.api.project.rest

import java.time.LocalDateTime
import java.util.UUID

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import com.projecthub.core.application.project.port.`in`.ProjectQueryUseCase
import com.projecthub.core.application.project.service.ProjectManagementService
import com.projecthub.core.domain.project.model.Project
import com.projecthub.core.domain.project.model.ProjectId
import com.projecthub.core.domain.project.model.ProjectPriority
import com.projecthub.core.domain.project.model.ProjectStatus
import com.projecthub.core.domain.project.model.UserId

/**
 * REST controller for project query operations.
 * This follows CQRS principles by separating read operations from write operations.
 */
@RestController
@RequestMapping("/api/v1/projects")
class ProjectQueryController(private val projectQueryUseCase: ProjectQueryUseCase) {

    /**
     * Gets a project by its ID.
     */
    @GetMapping("/{id}")
    fun getProjectById(@PathVariable("id") id: UUID): ResponseEntity<ProjectDetailResponse> {
        return projectQueryUseCase.getProjectById(ProjectId(id))
            .map { project -> ResponseEntity.ok(mapToDetailResponse(project)) }
            .orElse(ResponseEntity.notFound().build())
    }

    /**
     * Gets all projects.
     */
    @GetMapping
    fun getAllProjects(): ResponseEntity<List<ProjectSummaryResponse>> {
        val projects = projectQueryUseCase.getAllProjects()
            .map { this.mapToSummaryResponse(it) }

        return ResponseEntity.ok(projects)
    }

    /**
     * Gets projects by status.
     */
    @GetMapping("/status/{status}")
    fun getProjectsByStatus(@PathVariable("status") status: String): ResponseEntity<List<ProjectSummaryResponse>> {
        return try {
            val projectStatus = ProjectStatus.valueOf(status.toUpperCase())

            val projects = projectQueryUseCase.getProjectsByStatus(projectStatus)
                .map { this.mapToSummaryResponse(it) }

            ResponseEntity.ok(projects)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Gets projects by owner.
     */
    @GetMapping("/owner/{ownerId}")
    fun getProjectsByOwner(@PathVariable("ownerId") ownerId: UUID): ResponseEntity<List<ProjectSummaryResponse>> {
        val projects = projectQueryUseCase.getProjectsByOwner(UserId(ownerId))
            .map { this.mapToSummaryResponse(it) }

        return ResponseEntity.ok(projects)
    }

    /**
     * Gets projects by priority.
     */
    @GetMapping("/priority/{priority}")
    fun getProjectsByPriority(@PathVariable("priority") priority: String): ResponseEntity<List<ProjectSummaryResponse>> {
        return try {
            val projectPriority = ProjectPriority.valueOf(priority.toUpperCase())

            val projects = projectQueryUseCase.getProjectsByPriority(projectPriority)
                .map { this.mapToSummaryResponse(it) }

            ResponseEntity.ok(projects)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Gets projects created between the specified dates.
     */
    @GetMapping("/created-between")
    fun getProjectsCreatedBetween(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: LocalDateTime
    ): ResponseEntity<List<ProjectSummaryResponse>> {

        val projects = projectQueryUseCase.getProjectsCreatedBetween(from, to)
            .map { this.mapToSummaryResponse(it) }

        return ResponseEntity.ok(projects)
    }

    /**
     * Searches for projects by name or description.
     */
    @GetMapping("/search")
    fun searchProjects(@RequestParam("q") searchTerm: String?): ResponseEntity<List<ProjectSummaryResponse>> {
        if (searchTerm.isNullOrBlank()) {
            return ResponseEntity.badRequest().build()
        }

        val projects = projectQueryUseCase.searchProjects(searchTerm)
            .map { this.mapToSummaryResponse(it) }

        return ResponseEntity.ok(projects)
    }

    /**
     * Error handler for project not found exceptions.
     */
    @ExceptionHandler(ProjectManagementService.ProjectNotFoundException::class)
    fun handleProjectNotFound(
        ex: ProjectManagementService.ProjectNotFoundException
    ): ResponseEntity<ProjectController.ErrorResponse> {

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ProjectController.ErrorResponse(ex.message ?: "Project not found"))
    }

    // Mapper methods to convert domain entities to response DTOs

    private fun mapToSummaryResponse(project: Project): ProjectSummaryResponse {
        val response = ProjectSummaryResponse()
        response.id = project.id.value.toString()
        response.name = project.name.value
        response.status = project.status.name
        project.priority?.let { response.priority = it.name }
        return response
    }

    private fun mapToDetailResponse(project: Project): ProjectDetailResponse {
        val response = ProjectDetailResponse()
        response.id = project.id.value.toString()
        response.name = project.name.value
        response.description = project.description.value
        response.status = project.status.name
        response.createdAt = project.createdAt
        response.updatedAt = project.updatedAt
        project.priority?.let { response.priority = it.name }
        project.ownerId?.let { response.ownerId = it.value.toString() }
        return response
    }

    // Response DTOs

    class ProjectSummaryResponse {
        var id: String? = null
        var name: String? = null
        var status: String? = null
        var priority: String? = null
    }

    class ProjectDetailResponse {
        var id: String? = null
        var name: String? = null
        var description: String? = null
        var status: String? = null
        var createdAt: LocalDateTime? = null
        var updatedAt: LocalDateTime? = null
        var priority: String? = null
        var ownerId: String? = null
    }
}
