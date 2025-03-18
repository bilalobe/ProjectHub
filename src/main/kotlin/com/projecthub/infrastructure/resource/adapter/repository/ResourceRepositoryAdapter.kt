package com.projecthub.infrastructure.resource.adapter.repository

import com.projecthub.application.resource.domain.Resource
import com.projecthub.application.resource.domain.ResourceType
import com.projecthub.application.resource.port.repository.ResourceRepositoryPort
import org.springframework.stereotype.Component

@Component
class ResourceRepositoryAdapter(
    private val jpaRepository: ResourceJpaRepository
) : ResourceRepositoryPort {

    override fun save(resource: Resource): Resource {
        val entity = ResourceJpaEntity.fromDomain(resource)
        return jpaRepository.save(entity).toDomain()
    }

    override fun findById(id: String): Resource? {
        return jpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findAll(): List<Resource> {
        return jpaRepository.findAll().map { it.toDomain() }
    }

    override fun findByType(type: ResourceType): List<Resource> {
        return jpaRepository.findByType(type).map { it.toDomain() }
    }

    override fun findByAllocatedTo(projectId: String): List<Resource> {
        return jpaRepository.findByAllocatedTo(projectId).map { it.toDomain() }
    }

    override fun findAvailable(): List<Resource> {
        return jpaRepository.findAvailable().map { it.toDomain() }
    }

    override fun deleteById(id: String) {
        jpaRepository.deleteById(id)
    }
}
