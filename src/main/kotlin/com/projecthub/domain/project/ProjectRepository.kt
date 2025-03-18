package com.projecthub.domain.project

/**
 * Repository interface for projects (outbound port)
 * This interface defines the persistence operations available for Project entities
 */
interface ProjectRepository {
    fun findById(id: String): Project?
    fun findAll(): List<Project>
    fun save(project: Project): Project
    fun deleteById(id: String)
    fun findByOwnerId(ownerId: String): List<Project>
    fun findByTeamId(teamId: String): List<Project>
}
