package com.projecthub.infrastructure.adapter.in.web.project

import com.projecthub.core.application.project.port.`in`.ProjectManagementUseCase
import com.projecthub.core.application.project.dto.ProjectDto
import com.projecthub.core.application.project.dto.CreateProjectCommand

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import java.util.UUID

@RestController
@RequestMapping("/api/projects")
class ProjectController(private val projectManagementUseCase: ProjectManagementUseCase) {

    @PostMapping
    fun createProject(@RequestBody command: CreateProjectCommand): ResponseEntity<UUID> {
        return ResponseEntity.ok(projectManagementUseCase.createProject(command.name, command.description).value)
    }

    @GetMapping("/{id}")
    fun getProject(@PathVariable id: UUID): ResponseEntity<ProjectDto> {
        return projectManagementUseCase.getProject(ProjectId.fromUUID(id))
            .map { project -> ResponseEntity.ok(mapToDto(project)) }
            .orElse(ResponseEntity.notFound().build())
    }

    // Additional endpoints and mapping methods
}
