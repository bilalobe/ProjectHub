package com.projecthub.application.resource.port.api

import com.projecthub.application.resource.domain.ResourceType
import java.time.LocalDateTime

interface ResourceService {
    fun createResource(request: CreateResourceRequest): ResourceDto
    fun getResource(id: String): ResourceDto?
    fun updateResource(id: String, request: UpdateResourceRequest): ResourceDto?
    fun allocateResource(id: String, projectId: String): ResourceDto?
    fun deallocateResource(id: String): ResourceDto?
    fun listResources(filter: ResourceFilter): List<ResourceDto>
}

data class CreateResourceRequest(
    val name: String,
    val type: ResourceType
)

data class UpdateResourceRequest(
    val name: String? = null,
    val type: ResourceType? = null
)

data class ResourceFilter(
    val type: ResourceType? = null,
    val allocatedTo: String? = null
)

data class ResourceDto(
    val id: String,
    val name: String,
    val type: ResourceType,
    val allocatedTo: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
