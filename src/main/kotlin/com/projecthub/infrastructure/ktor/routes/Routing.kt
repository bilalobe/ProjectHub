package com.projecthub.infrastructure.ktor.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.*

fun Application.configureRouting() {
    routing {
        projectRoutes()

        get("/") {
            call.respondText("ProjectHub API")
        }

        get("/rabbitmq") {
            rabbitmq {
                basicPublish {
                    exchange = "test-exchange"
                    routingKey = "test-routing-key"
                    message { "Hello Ktor!" }
                }
            }
            call.respondText("Hello RabbitMQ!")
        }
    }
}
