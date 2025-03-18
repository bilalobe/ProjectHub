package com.projecthub.infrastructure.project.adapter.repository

import com.projecthub.application.project.domain.Project
import com.projecthub.application.project.domain.ProjectStatus
import java.time.LocalDateTime
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Column
import jakarta.persistence.Enumerated
import jakarta.persistence.EnumType

/**
 * JPA entity for project persistence
 * Contains all JPA annotations, keeping domain clean
 */
@Entity
@Table(name = "projects")
class ProjectJpaEntity(
    @Id
    val id: String,
    
    @Column(nullable = false)
    val name: String,
    
    @Column(nullable = false, length = 2000)
    val description: String,
    
    @Column(name = "owner_id", nullable = false)
    val ownerId: String,
    
    @Column(name = "team_id", nullable = false)
    val teamId: String,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: ProjectStatus,
    
    @Column(name = "completion_date")
    val completionDate: LocalDateTime?,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime
) {
    /**
     * Convert JPA entity to domain entity
     */
    fun toDomain(): Project {
        return Project.reconstitute(
            id = id,
            name = name,
            description = description,
            ownerId = ownerId,
            teamId = teamId,
            status = status,
            completionDate = completionDate,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    companion object {
        /**
         * Create JPA entity from domain entity
         */
        fun fromDomain(project: Project): ProjectJpaEntity {
            return ProjectJpaEntity(
                id = project.id,
                name = project.name,
                description = project.description,
                ownerId = project.ownerId,
                teamId = project.teamId,
                status = project.status,
                completionDate = project.completionDate,
                createdAt = project.createdAt,
                updatedAt = project.updatedAt
            )
        }
    }
}
