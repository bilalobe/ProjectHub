package com.projecthub.application.resource.domain

import java.time.LocalDateTime

/**
 * Core domain entity representing a resource
 */
class Resource private constructor(
    val id: String,
    private var _name: String,
    private var _type: ResourceType,
    private var _allocatedTo: String?,
    val createdAt: LocalDateTime,
    private var _updatedAt: LocalDateTime
) {
    val name: String get() = _name
    val type: ResourceType get() = _type
    val allocatedTo: String? get() = _allocatedTo
    val updatedAt: LocalDateTime get() = _updatedAt

    fun updateDetails(name: String?, type: ResourceType?) {
        name?.let { _name = it }
        type?.let { _type = it }
        _updatedAt = LocalDateTime.now()
    }

    fun allocateTo(projectId: String) {
        _allocatedTo = projectId
        _updatedAt = LocalDateTime.now()
    }

    fun deallocate() {
        _allocatedTo = null
        _updatedAt = LocalDateTime.now()
    }

    companion object {
        fun create(id: String, name: String, type: ResourceType): Resource {
            val now = LocalDateTime.now()
            return Resource(id, name, type, null, now, now)
        }

        fun reconstitute(
            id: String,
            name: String,
            type: ResourceType,
            allocatedTo: String?,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): Resource {
            return Resource(id, name, type, allocatedTo, createdAt, updatedAt)
        }
    }
}

enum class ResourceType {
    HUMAN,
    EQUIPMENT,
    MATERIAL
}
