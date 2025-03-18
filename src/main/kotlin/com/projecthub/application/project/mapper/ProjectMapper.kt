package com.projecthub.application.project.mapper

import com.projecthub.application.project.dto.ProjectDTO
import com.projecthub.domain.project.Project
import com.projecthub.infrastructure.persistence.project.jpa.ProjectJpaEntity
import org.springframework.stereotype.Component

/**
 * Mapper for converting between Project domain entities and DTOs/persistence entities
 */
@Component
class ProjectMapper {

    /**
     * Maps domain entity to DTO
     */
    fun toDto(project: Project): ProjectDTO {
        return ProjectDTO(
            id = project.id,
            name = project.name,
            description = project.description,
            ownerId = project.ownerId,
            teamId = project.teamId,
            completionDate = project.completionDate,
            createdAt = project.createdAt,
            updatedAt = project.updatedAt
        )
    }

    /**
     * Maps domain entity to JPA entity
     */
    fun toJpaEntity(project: Project): ProjectJpaEntity {
        return ProjectJpaEntity(
            id = project.id,
            name = project.name,
            description = project.description,
            ownerId = project.ownerId,
            teamId = project.teamId,
            completionDate = project.completionDate,
            createdAt = project.createdAt,
            updatedAt = project.updatedAt
        )
    }

    /**
     * Maps JPA entity to domain entity
     */
    fun toDomainEntity(entity: ProjectJpaEntity): Project {
        // Use reflection to create a Project instance since the constructor is private
        // In a real application, you might want to create a static factory method in the Project class
        return Project::class.java.getDeclaredConstructor(
            String::class.java,
            String::class.java,
            String::class.java,
            String::class.java,
            String::class.java,
            java.time.LocalDateTime::class.java,
            java.time.LocalDateTime::class.java,
            java.time.LocalDateTime::class.java
        ).apply {
            isAccessible = true
        }.newInstance(
            entity.id,
            entity.name,
            entity.description,
            entity.ownerId,
            entity.teamId,
            entity.completionDate,
            entity.createdAt,
            entity.updatedAt
        )
    }
}
