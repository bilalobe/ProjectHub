package com.projecthub.infrastructure.project.adapter.repository

import com.projecthub.application.project.domain.Project
import com.projecthub.application.project.domain.ProjectStatus
import com.projecthub.application.project.port.repository.ProjectRepositoryPort
import org.springframework.stereotype.Component

@Component
class ProjectRepositoryAdapter(
    private val jpaRepository: ProjectJpaRepository
) : ProjectRepositoryPort {

    override fun save(project: Project): Project {
        val entity = ProjectJpaEntity.fromDomain(project)
        return jpaRepository.save(entity).toDomain()
    }

    override fun findById(id: String): Project? {
        return jpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findAll(): List<Project> {
        return jpaRepository.findAll().map { it.toDomain() }
    }

    override fun findByOwnerId(ownerId: String): List<Project> {
        return jpaRepository.findByOwnerId(ownerId).map { it.toDomain() }
    }

    override fun findByTeamId(teamId: String): List<Project> {
        return jpaRepository.findByTeamId(teamId).map { it.toDomain() }
    }

    override fun findByStatus(status: ProjectStatus): List<Project> {
        return jpaRepository.findByStatus(status).map { it.toDomain() }
    }

    override fun findByStatusAndOwnerId(status: ProjectStatus, ownerId: String): List<Project> {
        return jpaRepository.findByStatusAndOwnerId(status, ownerId).map { it.toDomain() }
    }

    override fun deleteById(id: String) {
        jpaRepository.deleteById(id)
    }
}
