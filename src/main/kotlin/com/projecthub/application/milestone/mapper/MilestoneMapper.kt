package com.projecthub.application.milestone.mapper

import com.projecthub.application.milestone.dto.MilestoneDTO
import com.projecthub.domain.milestone.Milestone
import com.projecthub.infrastructure.persistence.milestone.jpa.MilestoneJpaEntity
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class MilestoneMapper {

    fun toDto(milestone: Milestone): MilestoneDTO {
        return MilestoneDTO(
            id = milestone.id,
            name = milestone.name,
            description = milestone.description,
            projectId = milestone.project.id,
            dueDate = milestone.dueDate,
            status = milestone.status,
            progress = milestone.progress,
            assigneeId = milestone.assignee?.id,
            completionDate = milestone.completionDate,
            createdAt = milestone.createdAt ?: LocalDateTime.now(),
            updatedAt = milestone.updatedAt ?: LocalDateTime.now(),
            dependencies = milestone.dependencies.map { it.id }
        )
    }

    fun toJpaEntity(milestone: Milestone): MilestoneJpaEntity {
        return MilestoneJpaEntity(
            id = milestone.id,
            name = milestone.name,
            description = milestone.description,
            projectId = milestone.project.id,
            dueDate = milestone.dueDate,
            status = milestone.status,
            progress = milestone.progress,
            assigneeId = milestone.assignee?.id,
            completionDate = milestone.completionDate,
            createdAt = milestone.createdAt ?: LocalDateTime.now(),
            updatedAt = milestone.updatedAt ?: LocalDateTime.now()
        )
    }

    fun toDomainEntity(entity: MilestoneJpaEntity): Milestone {
        // Use reflection since we have private constructor
        val constructor = Milestone::class.java.getDeclaredConstructor(
            String::class.java,             // id
            String::class.java,             // name
            String::class.java,             // description
            com.projecthub.domain.project.Project::class.java, // project
            java.time.LocalDate::class.java, // dueDate
            com.projecthub.domain.milestone.MilestoneStatus::class.java, // status
            Int::class.java,                // progress
            com.projecthub.domain.user.User::class.java, // assignee
            java.time.LocalDate::class.java, // completionDate
            java.time.LocalDateTime::class.java, // createdAt
            java.time.LocalDateTime::class.java  // updatedAt
        ).apply { isAccessible = true }

        return constructor.newInstance(
            entity.id,
            entity.name,
            entity.description,
            null, // project will be set by repository
            entity.dueDate,
            entity.status,
            entity.progress,
            null, // assignee will be set by repository
            entity.completionDate,
            entity.createdAt,
            entity.updatedAt
        )
    }
}
