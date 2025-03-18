package com.projecthub.infrastructure.project.adapter

import com.projecthub.application.project.domain.Project
import com.projecthub.application.project.port.ProjectRepositoryPort
import org.springframework.stereotype.Repository

@Repository
class ProjectRepositoryAdapter(
    private val jpaRepository: ProjectJpaRepository
) : ProjectRepositoryPort {

    override fun save(project: Project): Project {
        val entity = ProjectJpaEntity(
            id = project.id,
            name = project.name,
            description = project.description,
            ownerId = project.ownerId,
            teamId = project.teamId,
            completionDate = project.completionDate,
            createdAt = project.createdAt,
            updatedAt = project.updatedAt
        )
        return jpaRepository.save(entity).toDomain()
    }

    override fun findById(id: String): Project? {
        return jpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun deleteById(id: String) {
        jpaRepository.deleteById(id)
    }
}
