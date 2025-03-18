package com.projecthub.core.application.project.service

import java.time.LocalDateTime
import java.util.Optional
import java.util.stream.Collectors

import com.projecthub.core.application.project.port.`in`.ProjectQueryUseCase
import com.projecthub.core.application.project.port.out.ProjectRepository
import com.projecthub.core.domain.project.model.Project
import com.projecthub.core.domain.project.model.ProjectId
import com.projecthub.core.domain.project.model.ProjectPriority
import com.projecthub.core.domain.project.model.ProjectStatus
import com.projecthub.core.domain.project.model.UserId

/**
 * Implementation of the project query use cases.
 * This service provides read-only access to project data, following CQRS principles.
 */
class ProjectQueryService(private val projectRepository: ProjectRepository) : ProjectQueryUseCase {

    override fun getProjectById(id: ProjectId): Optional<Project> {
        return projectRepository.findById(id)
    }

    override fun getAllProjects(): List<Project> {
        return projectRepository.findAll()
    }

    override fun getProjectsByStatus(status: ProjectStatus): List<Project> {
        return projectRepository.findByStatus(status)
    }

    override fun getProjectsByOwner(ownerId: UserId): List<Project> {
        return projectRepository.findByOwnerId(ownerId)
    }

    override fun getProjectsByPriority(priority: ProjectPriority): List<Project> {
        // For simplicity, we filter in memory. In a real application, this would be
        // a dedicated repository method to avoid loading all projects into memory.
        return projectRepository.findAll().stream()
            .filter { p -> p.getPriority() == priority }
            .collect(Collectors.toList())
    }

    override fun getProjectsCreatedBetween(from: LocalDateTime, to: LocalDateTime): List<Project> {
        // For simplicity, we filter in memory. In a real application, this would be
        // a dedicated repository method to avoid loading all projects into memory.
        return projectRepository.findAll().stream()
            .filter { p -> !p.getCreatedAt().isBefore(from) && !p.getCreatedAt().isAfter(to) }
            .collect(Collectors.toList())
    }

    override fun searchProjects(searchTerm: String): List<Project> {
        if (searchTerm.isNullOrBlank()) {
            throw IllegalArgumentException("Search term cannot be empty")
        }

        val term = searchTerm.lowercase().trim()

        // For simplicity, we filter in memory. In a real application, this would be
        // a dedicated repository method, possibly using full-text search capabilities.
        return projectRepository.findAll().stream()
            .filter { p ->
                p.getName().getValue().lowercase().contains(term) ||
                    p.getDescription().getValue().lowercase().contains(term)
            }
            .collect(Collectors.toList())
    }
}
