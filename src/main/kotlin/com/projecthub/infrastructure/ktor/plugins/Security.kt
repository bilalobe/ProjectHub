package com.projecthub.infrastructure.ktor.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.csrf.*
import io.ktor.server.plugins.hsts.*
import io.ktor.server.sessions.*

fun Application.configureSecurity() {
    install(Authentication) {
        // Configure authentication mechanisms
    }

    install(CSRF) {
        // Configure CSRF protection
    }

    install(HSTS) {
        // Configure HTTP Strict Transport Security
    }

    install(Sessions) {
        // Configure sessions
    }
}
