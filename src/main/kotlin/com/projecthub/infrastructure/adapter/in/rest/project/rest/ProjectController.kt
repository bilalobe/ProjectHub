package com.projecthub.infrastructure.api.project.rest

import java.util.UUID

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import com.projecthub.core.application.project.port.`in`.ProjectManagementUseCase
import com.projecthub.core.application.project.service.ProjectManagementService
import com.projecthub.core.domain.project.model.ProjectDescription
import com.projecthub.core.domain.project.model.ProjectId
import com.projecthub.core.domain.project.model.ProjectName

/**
 * REST controller for project management operations.
 * This is an adapter in the hexagonal architecture that translates HTTP requests into domain use case calls.
 */
@RestController
@RequestMapping("/api/v1/projects")
class ProjectController(private val projectManagementUseCase: ProjectManagementUseCase) {

    /**
     * Creates a new project.
     */
    @PostMapping
    fun createProject(@RequestBody request: CreateProjectRequest): ResponseEntity<ProjectResponse> {
        val projectId = projectManagementUseCase.createProject(
            ProjectName(request.name),
            ProjectDescription(request.description)
        )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ProjectResponse(projectId.value.toString()))
    }

    /**
     * Updates a project's name.
     */
    @PutMapping("/{id}/name")
    fun updateProjectName(
        @PathVariable("id") id: UUID,
        @RequestBody request: UpdateProjectNameRequest
    ): ResponseEntity<Void> {

        projectManagementUseCase.updateProjectName(
            ProjectId(id),
            ProjectName(request.name)
        )

        return ResponseEntity.ok().build()
    }

    /**
     * Updates a project's description.
     */
    @PutMapping("/{id}/description")
    fun updateProjectDescription(
        @PathVariable("id") id: UUID,
        @RequestBody request: UpdateProjectDescriptionRequest
    ): ResponseEntity<Void> {

        projectManagementUseCase.updateProjectDescription(
            ProjectId(id),
            ProjectDescription(request.description)
        )

        return ResponseEntity.ok().build()
    }

    /**
     * Activates a project.
     */
    @PostMapping("/{id}/activate")
    fun activateProject(@PathVariable("id") id: UUID): ResponseEntity<Void> {
        projectManagementUseCase.activateProject(ProjectId(id))
        return ResponseEntity.ok().build()
    }

    /**
     * Archives a project.
     */
    @PostMapping("/{id}/archive")
    fun archiveProject(@PathVariable("id") id: UUID): ResponseEntity<Void> {
        projectManagementUseCase.archiveProject(ProjectId(id))
        return ResponseEntity.ok().build()
    }

    /**
     * Error handler for project not found exceptions.
     */
    @ExceptionHandler(ProjectManagementService.ProjectNotFoundException::class)
    fun handleProjectNotFound(
        ex: ProjectManagementService.ProjectNotFoundException
    ): ResponseEntity<ErrorResponse> {

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(ex.message ?: "Project not found"))
    }

    // Request and response classes

    class CreateProjectRequest {
        var name: String = ""
        var description: String = ""
    }

    class UpdateProjectNameRequest {
        var name: String = ""
    }

    class UpdateProjectDescriptionRequest {
        var description: String = ""
    }

    data class ProjectResponse(val id: String)

    data class ErrorResponse(val message: String)
}
