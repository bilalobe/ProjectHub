package com.projecthub.application.project.service

import com.projecthub.application.project.domain.Project
import com.projecthub.application.project.domain.ProjectStatus
import com.projecthub.application.project.event.*
import com.projecthub.application.project.port.api.*
import com.projecthub.application.project.port.event.ProjectEventPublisher
import com.projecthub.application.project.port.repository.ProjectRepositoryPort
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class ProjectServiceImpl(
    private val projectRepository: ProjectRepositoryPort,
    private val eventPublisher: ProjectEventPublisher
) : ProjectService {

    override fun createProject(request: CreateProjectRequest): ProjectDto {
        val projectId = UUID.randomUUID().toString()
        
        val project = Project.create(
            id = projectId,
            name = request.name,
            description = request.description,
            ownerId = request.ownerId,
            teamId = request.teamId
        )
        
        val savedProject = projectRepository.save(project)
        
        // Publish domain event
        eventPublisher.publish(
            ProjectCreatedEvent(
                id = savedProject.id,
                name = savedProject.name,
                description = savedProject.description,
                ownerId = savedProject.ownerId,
                teamId = savedProject.teamId
            )
        )
        
        return savedProject.toDto()
    }

    override fun getProject(id: String): ProjectDto? {
        return projectRepository.findById(id)?.toDto()
    }

    override fun updateProject(id: String, request: UpdateProjectRequest): ProjectDto? {
        val project = projectRepository.findById(id) ?: return null
        
        val updatedFields = mutableMapOf<String, Any?>()
        
        if (request.name != null || request.description != null) {
            project.updateDetails(request.name, request.description)
            
            request.name?.let { updatedFields["name"] = it }
            request.description?.let { updatedFields["description"] = it }
        }
        
        if (request.completionDate != null) {
            project.setCompletionDate(request.completionDate)
            updatedFields["completionDate"] = request.completionDate
        }
        
        val savedProject = projectRepository.save(project)
        
        // Publish event if there were changes
        if (updatedFields.isNotEmpty()) {
            eventPublisher.publish(
                ProjectUpdatedEvent(
                    id = savedProject.id,
                    updatedFields = updatedFields
                )
            )
        }
        
        return savedProject.toDto()
    }

    override fun changeStatus(id: String, status: ProjectStatus): ProjectDto? {
        val project = projectRepository.findById(id) ?: return null
        
        val previousStatus = project.status
        project.updateStatus(status)
        
        val savedProject = projectRepository.save(project)
        
        // Publish domain event
        eventPublisher.publish(
            ProjectStatusChangedEvent(
                id = savedProject.id,
                previousStatus = previousStatus,
                newStatus = status
            )
        )
        
        return savedProject.toDto()
    }

    override fun assignTeam(id: String, teamId: String): ProjectDto? {
        val project = projectRepository.findById(id) ?: return null
        
        val previousTeamId = project.teamId
        project.assignTeam(teamId)
        
        val savedProject = projectRepository.save(project)
        
        // Publish domain event
        eventPublisher.publish(
            ProjectTeamAssignedEvent(
                id = savedProject.id,
                previousTeamId = previousTeamId,
                newTeamId = teamId
            )
        )
        
        return savedProject.toDto()
    }

    override fun listProjects(filter: ProjectFilter): List<ProjectDto> {
        val projects = when {
            filter.ownerId != null -> projectRepository.findByOwnerId(filter.ownerId)
            filter.teamId != null -> projectRepository.findByTeamId(filter.teamId)
            filter.status != null -> projectRepository.findByStatus(filter.status)
            else -> projectRepository.findAll()
        }
        
        return projects.map { it.toDto() }
    }

    override fun deleteProject(id: String) {
        projectRepository.findById(id)?.let {
            projectRepository.deleteById(id)
            
            // Publish domain event
            eventPublisher.publish(
                ProjectDeletedEvent(id = id)
            )
        }
    }
    
    // Example of consuming events (optional)
    fun consumeEvents() = runBlocking {
        (eventPublisher as? KotlinFlowEventPublisher)?.events?.collect { event ->
            println("Event received: $event")
        }
    }
    
    // Extension function to convert domain entity to DTO
    private fun Project.toDto(): ProjectDto {
        return ProjectDto(
            id = id,
            name = name,
            description = description,
            ownerId = ownerId,
            teamId = teamId,
            status = status,
            completionDate = completionDate,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    fun listProjectsByStatusAndOwner(status: ProjectStatus, ownerId: String): List<ProjectDto> {
        val projects = projectRepository.findByStatusAndOwnerId(status, ownerId)
        return projects.map { it.toDto() }
    }
}
