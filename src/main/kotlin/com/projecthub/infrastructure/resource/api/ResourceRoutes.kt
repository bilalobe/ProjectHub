package com.projecthub.infrastructure.resource.api

import com.projecthub.application.resource.port.api.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.resourceRoutes(resourceService: ResourceService) {
    route("/api/v1/resources") {
        post {
            val request = call.receive<CreateResourceRequest>()
            val resource = resourceService.createResource(request)
            call.respond(HttpStatusCode.Created, resource)
        }

        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val resource = resourceService.getResource(id)
            if (resource != null) {
                call.respond(resource)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        put("/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val request = call.receive<UpdateResourceRequest>()
            val resource = resourceService.updateResource(id, request)
            if (resource != null) {
                call.respond(resource)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        patch("/{id}/allocate") {
            val id = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest)
            val projectId = call.receive<String>()
            val resource = resourceService.allocateResource(id, projectId)
            if (resource != null) {
                call.respond(resource)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        patch("/{id}/deallocate") {
            val id = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest)
            val resource = resourceService.deallocateResource(id)
            if (resource != null) {
                call.respond(resource)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get {
            val type = call.request.queryParameters["type"]?.let { ResourceType.valueOf(it) }
            val allocatedTo = call.request.queryParameters["allocatedTo"]
            val filter = ResourceFilter(type, allocatedTo)
            val resources = resourceService.listResources(filter)
            call.respond(resources)
        }
    }
}
