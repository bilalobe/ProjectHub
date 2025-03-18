package com.projecthub.application.project.service

import com.projecthub.application.project.port.`in`.ProjectManagementUseCase
import com.projecthub.application.project.dto.ProjectDTO
import com.projecthub.application.project.dto.CreateProjectCommand
import com.projecthub.application.project.dto.UpdateProjectCommand
import com.projecthub.application.project.mapper.ProjectMapper
import com.projecthub.domain.event.DomainEventPublisher
import com.projecthub.domain.project.ProjectRepository
import com.projecthub.domain.project.Project
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * Service implementing the ProjectManagementUseCase interface
 * This class orchestrates the application flow between domain model and infrastructure
 */
@Service
class ProjectManagementService(
    private val projectRepository: ProjectRepository,
    private val projectMapper: ProjectMapper,
    private val eventPublisher: DomainEventPublisher
) : ProjectManagementUseCase {

    @Transactional
    override fun createProject(command: CreateProjectCommand): ProjectDTO {
        // Generate new project ID
        val projectId = UUID.randomUUID().toString()

        // Create domain object using static factory method
        val project = Project.create(
            id = projectId,
            name = command.name,
            description = command.description,
            ownerId = command.ownerId
        )

        // Save to repository
        val savedProject = projectRepository.save(project)

        // Publish any domain events
        project.domainEvents.forEach { eventPublisher.publish(it) }
        project.clearEvents()

        // Return DTO
        return projectMapper.toDto(savedProject)
    }

    @Transactional
    override fun updateProject(projectId: String, command: UpdateProjectCommand): ProjectDTO {
        // Fetch project from repository
        val project = projectRepository.findById(projectId)
            ?: throw IllegalArgumentException("Project not found with ID: $projectId")

        // Apply update to domain entity (which will register domain events)
        project.updateDetails(command.name, command.description)

        // Save updated project
        val updatedProject = projectRepository.save(project)

        // Publish domain events
        project.domainEvents.forEach { eventPublisher.publish(it) }
        project.clearEvents()

        // Return updated DTO
        return projectMapper.toDto(updatedProject)
    }

    @Transactional
    override fun assignProjectToTeam(projectId: String, teamId: String): ProjectDTO {
        val project = projectRepository.findById(projectId)
            ?: throw IllegalArgumentException("Project not found with ID: $projectId")

        project.assignToTeam(teamId)

        val updatedProject = projectRepository.save(project)

        // Publish domain events
        project.domainEvents.forEach { eventPublisher.publish(it) }
        project.clearEvents()

        return projectMapper.toDto(updatedProject)
    }

    @Transactional
    override fun completeProject(projectId: String): ProjectDTO {
        val project = projectRepository.findById(projectId)
            ?: throw IllegalArgumentException("Project not found with ID: $projectId")

        project.complete()

        val updatedProject = projectRepository.save(project)

        // Publish domain events
        project.domainEvents.forEach { eventPublisher.publish(it) }
        project.clearEvents()

        return projectMapper.toDto(updatedProject)
    }

    @Transactional(readOnly = true)
    override fun getProjectById(projectId: String): ProjectDTO? {
        return projectRepository.findById(projectId)?.let { projectMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    override fun getAllProjects(): List<ProjectDTO> {
        return projectRepository.findAll().map { projectMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    override fun getProjectsByOwner(ownerId: String): List<ProjectDTO> {
        return projectRepository.findByOwnerId(ownerId).map { projectMapper.toDto(it) }
    }
}
