package com.projecthub.infrastructure.project.api

import com.projecthub.application.project.port.api.*
import com.projecthub.application.project.domain.ProjectStatus
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.projectRoutes(projectService: ProjectService) {
    route("/api/v1/projects") {
        post {
            val request = call.receive<CreateProjectRequest>()
            val project = projectService.createProject(request)
            call.respond(HttpStatusCode.Created, project)
        }

        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val project = projectService.getProject(id)
            if (project != null) {
                call.respond(project)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        put("/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val request = call.receive<UpdateProjectRequest>()
            val project = projectService.updateProject(id, request)
            if (project != null) {
                call.respond(project)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        patch("/{id}/status") {
            val id = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest)
            val statusUpdate = call.receive<StatusUpdateRequest>()
            val project = projectService.changeStatus(id, statusUpdate.status)
            if (project != null) {
                call.respond(project)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        patch("/{id}/team") {
            val id = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest)
            val teamAssignment = call.receive<TeamAssignmentRequest>()
            val project = projectService.assignTeam(id, teamAssignment.teamId)
            if (project != null) {
                call.respond(project)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get {
            val ownerId = call.request.queryParameters["ownerId"]
            val teamId = call.request.queryParameters["teamId"]
            val status = call.request.queryParameters["status"]?.let { ProjectStatus.valueOf(it) }
            val filter = ProjectFilter(ownerId, teamId, status)
            val projects = projectService.listProjects(filter)
            call.respond(projects)
        }

        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            projectService.deleteProject(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
