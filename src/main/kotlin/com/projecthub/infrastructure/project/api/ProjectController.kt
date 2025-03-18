package com.projecthub.infrastructure.project.api

import com.projecthub.application.project.domain.ProjectStatus
import com.projecthub.application.project.port.api.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/projects")
class ProjectController(
    private val projectService: ProjectService
) {

    @PostMapping
    fun createProject(@RequestBody request: CreateProjectRequest): ResponseEntity<ProjectDto> {
        val project = projectService.createProject(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(project)
    }

    @GetMapping("/{id}")
    fun getProject(@PathVariable id: String): ResponseEntity<ProjectDto> {
        return projectService.getProject(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PutMapping("/{id}")
    fun updateProject(
        @PathVariable id: String, 
        @RequestBody request: UpdateProjectRequest
    ): ResponseEntity<ProjectDto> {
        return projectService.updateProject(id, request)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PatchMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: String,
        @RequestBody statusUpdate: StatusUpdateRequest
    ): ResponseEntity<ProjectDto> {
        return projectService.changeStatus(id, statusUpdate.status)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PatchMapping("/{id}/team")
    fun assignTeam(
        @PathVariable id: String,
        @RequestBody teamAssignment: TeamAssignmentRequest
    ): ResponseEntity<ProjectDto> {
        return projectService.assignTeam(id, teamAssignment.teamId)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping
    fun listProjects(
        @RequestParam(required = false) ownerId: String?,
        @RequestParam(required = false) teamId: String?,
        @RequestParam(required = false) status: ProjectStatus?
    ): ResponseEntity<List<ProjectDto>> {
        val filter = ProjectFilter(
            ownerId = ownerId,
            teamId = teamId,
            status = status
        )
        return ResponseEntity.ok(projectService.listProjects(filter))
    }

    @DeleteMapping("/{id}")
    fun deleteProject(@PathVariable id: String): ResponseEntity<Unit> {
        projectService.deleteProject(id)
        return ResponseEntity.noContent().build()
    }
}

data class StatusUpdateRequest(val status: ProjectStatus)
data class TeamAssignmentRequest(val teamId: String)
