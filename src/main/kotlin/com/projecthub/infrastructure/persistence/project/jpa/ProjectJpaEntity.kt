package com.projecthub.infrastructure.persistence.project.jpa

import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * JPA entity representing a project in the database.
 */
@Entity
@Table(name = "projects")
class ProjectJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null
    var name: String? = null
    var description: String? = null
    var status: ProjectStatusJpa? = null
    var createdAt: LocalDateTime? = null
    var updatedAt: LocalDateTime? = null
    var priority: String? = null
    var category: String? = null
    var tags: String? = null
    var deadline: LocalDateTime? = null
    var ownerId: UUID? = null

    // Enum for project status in JPA
    enum class ProjectStatusJpa {
        DRAFT,
        ACTIVE,
        ARCHIVED
    }
}
