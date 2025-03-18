package com.projecthub.application.resource.event

import com.projecthub.application.resource.domain.ResourceType
import java.time.LocalDateTime

/**
 * Base class for all resource domain events
 */
sealed class ResourceEvent(
    val resourceId: String,
    val occurredAt: LocalDateTime = LocalDateTime.now(),
    val version: String = "1.0"
)

data class ResourceCreatedEvent(
    val id: String,
    val name: String,
    val type: ResourceType
) : ResourceEvent(id)

data class ResourceUpdatedEvent(
    val id: String,
    val updatedFields: Map<String, Any?>
) : ResourceEvent(id)

data class ResourceAllocatedEvent(
    val id: String,
    val projectId: String,
    val previousAllocation: String?
) : ResourceEvent(id)

data class ResourceDeallocatedEvent(
    val id: String,
    val previousAllocation: String
) : ResourceEvent(id)

data class ResourceDeletedEvent(
    val id: String
) : ResourceEvent(id)
