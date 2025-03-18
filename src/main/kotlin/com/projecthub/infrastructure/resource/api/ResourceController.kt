package com.projecthub.infrastructure.resource.api

import com.projecthub.application.resource.domain.ResourceType
import com.projecthub.application.resource.port.api.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/resources")
class ResourceController(
    private val resourceService: ResourceService
) {

    @PostMapping
    fun createResource(@RequestBody request: CreateResourceRequest): ResponseEntity<ResourceDto> {
        val resource = resourceService.createResource(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(resource)
    }

    @GetMapping("/{id}")
    fun getResource(@PathVariable id: String): ResponseEntity<ResourceDto> {
        return resourceService.getResource(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PutMapping("/{id}")
    fun updateResource(
        @PathVariable id: String,
        @RequestBody request: UpdateResourceRequest
    ): ResponseEntity<ResourceDto> {
        return resourceService.updateResource(id, request)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PatchMapping("/{id}/allocate")
    fun allocateResource(
        @PathVariable id: String,
        @RequestBody allocationRequest: ResourceAllocationRequest
    ): ResponseEntity<ResourceDto> {
        return resourceService.allocateResource(id, allocationRequest.projectId)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PatchMapping("/{id}/deallocate")
    fun deallocateResource(@PathVariable id: String): ResponseEntity<ResourceDto> {
        return resourceService.deallocateResource(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping
    fun listResources(
        @RequestParam(required = false) type: ResourceType?,
        @RequestParam(required = false) allocatedTo: String?
    ): ResponseEntity<List<ResourceDto>> {
        val filter = ResourceFilter(type, allocatedTo)
        return ResponseEntity.ok(resourceService.listResources(filter))
    }

    @DeleteMapping("/{id}")
    fun deleteResource(@PathVariable id: String): ResponseEntity<Unit> {
        resourceService.getResource(id)?.let {
            return ResponseEntity.noContent().build()
        } ?: return ResponseEntity.notFound().build()
    }
}

data class ResourceAllocationRequest(val projectId: String)
