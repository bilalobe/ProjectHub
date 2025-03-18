package com.projecthub.infrastructure.project.adapter

import com.projecthub.application.project.domain.Project
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class ProjectJpaEntity(
    @Id
    val id: String,
    val name: String,
    val description: String,
    val ownerId: String,
    val teamId: String,
    val completionDate: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    fun toDomain(): Project {
        return Project(
            id = id,
            name = name,
            description = description,
            ownerId = ownerId,
            teamId = teamId,
            completionDate = completionDate,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
