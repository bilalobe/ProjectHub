package com.projecthub.application.resource.service

import com.projecthub.application.resource.domain.Resource
import com.projecthub.application.resource.domain.ResourceType
import com.projecthub.application.resource.event.ResourceAllocatedEvent
import com.projecthub.application.resource.event.ResourceCreatedEvent
import com.projecthub.application.resource.event.ResourceDeallocatedEvent
import com.projecthub.application.resource.event.ResourceUpdatedEvent
import com.projecthub.application.resource.port.api.*
import com.projecthub.application.resource.port.event.ResourceEventPublisher
import com.projecthub.application.resource.port.repository.ResourceRepositoryPort
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class ResourceServiceImpl(
    private val resourceRepository: ResourceRepositoryPort,
    private val eventPublisher: ResourceEventPublisher
) : ResourceService {

    override fun createResource(request: CreateResourceRequest): ResourceDto {
        val resourceId = UUID.randomUUID().toString()

        val resource = Resource.create(
            id = resourceId,
            name = request.name,
            type = request.type
        )

        val savedResource = resourceRepository.save(resource)

        // Publish domain event
        eventPublisher.publish(
            ResourceCreatedEvent(
                id = savedResource.id,
                name = savedResource.name,
                type = savedResource.type
            )
        )

        return savedResource.toDto()
    }

    override fun getResource(id: String): ResourceDto? {
        return resourceRepository.findById(id)?.toDto()
    }

    override fun updateResource(id: String, request: UpdateResourceRequest): ResourceDto? {
        val resource = resourceRepository.findById(id) ?: return null

        val updatedFields = mutableMapOf<String, Any?>()

        resource.updateDetails(request.name, request.type)
        
        request.name?.let { updatedFields["name"] = it }
        request.type?.let { updatedFields["type"] = it }
        
        val savedResource = resourceRepository.save(resource)
        
        // Publish event if there were changes
        if (updatedFields.isNotEmpty()) {
            eventPublisher.publish(
                ResourceUpdatedEvent(
                    id = savedResource.id,
                    updatedFields = updatedFields
                )
            )
        }
        
        return savedResource.toDto()
    }

    override fun allocateResource(id: String, projectId: String): ResourceDto? {
        val resource = resourceRepository.findById(id) ?: return null
        
        val previousAllocation = resource.allocatedTo
        resource.allocateTo(projectId)
        
        val savedResource = resourceRepository.save(resource)
        
        // Publish domain event
        eventPublisher.publish(
            ResourceAllocatedEvent(
                id = savedResource.id,
                projectId = projectId,
                previousAllocation = previousAllocation
            )
        )
        
        return savedResource.toDto()
    }

    override fun deallocateResource(id: String): ResourceDto? {
        val resource = resourceRepository.findById(id) ?: return null
        
        val previousAllocation = resource.allocatedTo
        resource.deallocate()
        
        val savedResource = resourceRepository.save(resource)
        
        // Publish domain event only if it was previously allocated
        if (previousAllocation != null) {
            eventPublisher.publish(
                ResourceDeallocatedEvent(
                    id = savedResource.id,
                    previousAllocation = previousAllocation
                )
            )
        }
        
        return savedResource.toDto()
    }

    override fun listResources(filter: ResourceFilter): List<ResourceDto> {
        val resources = when {
            filter.type != null -> resourceRepository.findByType(filter.type)
            filter.allocatedTo != null -> resourceRepository.findByAllocatedTo(filter.allocatedTo)
            else -> resourceRepository.findAll()
        }
        
        return resources.map { it.toDto() }
    }
    
    // Extension function to convert domain entity to DTO
    private fun Resource.toDto(): ResourceDto {
        return ResourceDto(
            id = id,
            name = name,
            type = type,
            allocatedTo = allocatedTo,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
