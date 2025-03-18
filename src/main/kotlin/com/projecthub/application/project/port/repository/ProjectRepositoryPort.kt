package com.projecthub.application.project.port.repository

import com.projecthub.application.project.domain.Project
import com.projecthub.application.project.domain.ProjectStatus

/**
 * Repository port for accessing project data
 * Technology-agnostic interface for persistence operations
 */
interface ProjectRepositoryPort {
    fun save(project: Project): Project
    fun findById(id: String): Project?
    fun findAll(): List<Project>
    fun findByOwnerId(ownerId: String): List<Project>
    fun findByTeamId(teamId: String): List<Project>
    fun findByStatus(status: ProjectStatus): List<Project>
    fun findByStatusAndOwnerId(status: ProjectStatus, ownerId: String): List<Project>
    fun deleteById(id: String)
}
