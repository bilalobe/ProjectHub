package com.projecthub.infrastructure.ktor.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*

fun Route.projectRoutes() {
    route("/projects") {
        get {
            // Handle fetching all projects
            call.respond(HttpStatusCode.OK, "List of projects")
        }

        post {
            // Handle creating a new project
            call.respond(HttpStatusCode.Created, "Project created")
        }

        route("/{id}") {
            get {
                // Handle fetching a project by ID
                val id = call.parameters["id"]
                call.respond(HttpStatusCode.OK, "Project with ID: $id")
            }

            put {
                // Handle updating a project
                val id = call.parameters["id"]
                call.respond(HttpStatusCode.OK, "Project $id updated")
            }

            delete {
                // Handle deleting a project
                val id = call.parameters["id"]
                call.respond(HttpStatusCode.OK, "Project $id deleted")
            }
        }
    }
}
