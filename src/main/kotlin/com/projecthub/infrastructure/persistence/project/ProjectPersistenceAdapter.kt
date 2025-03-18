package com.projecthub.infrastructure.persistence.project

import com.projecthub.application.project.mapper.ProjectMapper
import com.projecthub.domain.project.Project
import com.projecthub.domain.project.ProjectRepository
import com.projecthub.infrastructure.persistence.project.jpa.ProjectJpaRepository
import org.springframework.stereotype.Component

/**
 * Adapter implementing the ProjectRepository interface
 * This adapter connects the domain layer to the persistence infrastructure
 */
@Component
class ProjectPersistenceAdapter(
    private val projectJpaRepository: ProjectJpaRepository,
    private val projectMapper: ProjectMapper
) : ProjectRepository {

    override fun findById(id: String): Project? {
        return projectJpaRepository.findById(id)
            .map { projectMapper.toDomainEntity(it) }
            .orElse(null)
    }

    override fun findAll(): List<Project> {
        return projectJpaRepository.findAll().map { projectMapper.toDomainEntity(it) }
    }

    override fun save(project: Project): Project {
        val jpaEntity = projectMapper.toJpaEntity(project)
        val savedEntity = projectJpaRepository.save(jpaEntity)
        return projectMapper.toDomainEntity(savedEntity)
    }

    override fun deleteById(id: String) {
        projectJpaRepository.deleteById(id)
    }

    override fun findByOwnerId(ownerId: String): List<Project> {
        return projectJpaRepository.findByOwnerId(ownerId)
            .map { projectMapper.toDomainEntity(it) }
    }

    override fun findByTeamId(teamId: String): List<Project> {
        return projectJpaRepository.findByTeamId(teamId)
            .map { projectMapper.toDomainEntity(it) }
    }
}
