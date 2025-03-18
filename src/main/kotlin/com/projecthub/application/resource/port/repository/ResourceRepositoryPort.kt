package com.projecthub.application.resource.port.repository

import com.projecthub.application.resource.domain.Resource
import com.projecthub.application.resource.domain.ResourceType

/**
 * Repository port for accessing resource data
 * Technology-agnostic interface for persistence operations
 */
interface ResourceRepositoryPort {
    fun save(resource: Resource): Resource
    fun findById(id: String): Resource?
    fun findAll(): List<Resource>
    fun findByType(type: ResourceType): List<Resource>
    fun findByAllocatedTo(projectId: String): List<Resource>
    fun findAvailable(): List<Resource> // Resources not allocated
    fun deleteById(id: String)
}
