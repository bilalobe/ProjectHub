package com.projecthub.infrastructure.resource.adapter.repository

import com.projecthub.application.resource.domain.ResourceType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ResourceJpaRepository : JpaRepository<ResourceJpaEntity, String> {
    fun findByType(type: ResourceType): List<ResourceJpaEntity>
    fun findByAllocatedTo(projectId: String): List<ResourceJpaEntity>
    
    @Query("SELECT r FROM ResourceJpaEntity r WHERE r.allocatedTo IS NULL")
    fun findAvailable(): List<ResourceJpaEntity>
}
