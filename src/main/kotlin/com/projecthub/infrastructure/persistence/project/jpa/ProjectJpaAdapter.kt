package com.projecthub.infrastructure.persistence.project.jpa

import java.util.Optional
import java.util.UUID

import org.springframework.stereotype.Component

import com.projecthub.core.application.project.port.out.ProjectRepository
import com.projecthub.core.domain.project.model.Project
import com.projecthub.core.domain.project.model.ProjectDescription
import com.projecthub.core.domain.project.model.ProjectId
import com.projecthub.core.domain.project.model.ProjectName
import com.projecthub.core.domain.project.model.ProjectStatus
import com.projecthub.core.domain.project.model.UserId
import com.projecthub.infrastructure.persistence.project.jpa.ProjectJpaEntity.ProjectStatusJpa

/**
 * Adapter that implements the domain's ProjectRepository interface using JPA.
 * This class connects our domain model to the persistence infrastructure.
 */
@Component
class ProjectJpaAdapter(private val jpaRepository: ProjectJpaRepository) : ProjectRepository {

    override fun save(project: Project) {
        val jpaEntity = mapToJpaEntity(project)
        jpaRepository.save(jpaEntity)
    }

    override fun findById(id: ProjectId): Optional<Project> {
        return jpaRepository.findById(id.value)
            .map { this.mapToDomainEntity(it) }
    }

    override fun findAll(): List<Project> {
        return jpaRepository.findAll()
            .map { this.mapToDomainEntity(it) }
    }

    override fun findByStatus(status: ProjectStatus): List<Project> {
        return jpaRepository.findByStatus(mapToJpaStatus(status))
            .map { this.mapToDomainEntity(it) }
    }

    override fun findByOwnerId(ownerId: UserId): List<Project> {
        return jpaRepository.findByOwnerId(ownerId.value)
            .map { this.mapToDomainEntity(it) }
    }

    override fun delete(id: ProjectId) {
        jpaRepository.deleteById(id.value)
    }

    /**
     * Maps a domain entity to a JPA entity.
     */
    private fun mapToJpaEntity(project: Project): ProjectJpaEntity {
        val entity = ProjectJpaEntity()
        entity.id = project.id.value
        entity.name = project.name.value
        entity.description = project.description.value
        entity.status = mapToJpaStatus(project.status)
        entity.createdAt = project.createdAt
        entity.updatedAt = project.updatedAt

        project.priority?.let { entity.priority = it.name }
        project.ownerId?.let { entity.ownerId = it.value }

        // Additional mapping for new fields would be implemented here

        return entity
    }

    /**
     * Maps a JPA entity to a domain entity.
     */
    private fun mapToDomainEntity(jpaEntity: ProjectJpaEntity): Project {
        val project = Project(
            ProjectId(jpaEntity.id!!),
            ProjectName(jpaEntity.name!!),
            ProjectDescription(jpaEntity.description!!)
        )

        // Reflection or another approach would be needed here to set the status, createdAt, and updatedAt fields
        // since they are not exposed through the constructor. In a real implementation, we might use
        // a more sophisticated mapping approach or adjust the domain model to accommodate this.

        return project
    }

    /**
     * Maps a domain status enum to a JPA status enum.
     */
    private fun mapToJpaStatus(status: ProjectStatus): ProjectStatusJpa {
        return when (status) {
            ProjectStatus.DRAFT -> ProjectStatusJpa.DRAFT
            ProjectStatus.ACTIVE -> ProjectStatusJpa.ACTIVE
            ProjectStatus.ARCHIVED -> ProjectStatusJpa.ARCHIVED
            else -> throw IllegalArgumentException("Unknown status: $status")
        }
    }

    /**
     * Maps a JPA status enum to a domain status enum.
     */
    private fun mapToDomainStatus(status: ProjectStatusJpa): ProjectStatus {
        return when (status) {
            ProjectStatusJpa.DRAFT -> ProjectStatus.DRAFT
            ProjectStatusJpa.ACTIVE -> ProjectStatus.ACTIVE
            ProjectStatusJpa.ARCHIVED -> ProjectStatus.ARCHIVED
        }
    }
}
