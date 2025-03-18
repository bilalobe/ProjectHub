package com.projecthub.session

import io.ktor.server.auth.*

/**
 * Session data for authenticated users
 */
data class UserSession(
    val userId: String,
    val roles: List<String> = emptyList()
) : Principal
