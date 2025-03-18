package com.projecthub.infrastructure.resource.adapter.repository

import com.projecthub.application.resource.domain.Resource
import com.projecthub.application.resource.domain.ResourceType
import java.time.LocalDateTime
import jakarta.persistence.*

/**
 * JPA entity for resource persistence
 * Contains all JPA annotations, keeping domain clean
 */
@Entity
@Table(name = "resources")
class ResourceJpaEntity(
    @Id
    val id: String,
    
    @Column(nullable = false)
    val name: String,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: ResourceType,
    
    @Column(name = "allocated_to")
    val allocatedTo: String?,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime
) {
    /**
     * Convert JPA entity to domain entity
     */
    fun toDomain(): Resource {
        return Resource.reconstitute(
            id = id,
            name = name,
            type = type,
            allocatedTo = allocatedTo,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    companion object {
        /**
         * Create JPA entity from domain entity
         */
        fun fromDomain(resource: Resource): ResourceJpaEntity {
            return ResourceJpaEntity(
                id = resource.id,
                name = resource.name,
                type = resource.type,
                allocatedTo = resource.allocatedTo,
                createdAt = resource.createdAt,
                updatedAt = resource.updatedAt
            )
        }
    }
}
