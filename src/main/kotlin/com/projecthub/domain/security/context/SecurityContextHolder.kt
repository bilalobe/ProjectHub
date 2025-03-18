package com.projecthub.domain.security.context

import com.projecthub.domain.user.User
import org.springframework.stereotype.Component

/**
 * Service for accessing the current authenticated user from anywhere in the application.
 * This abstracts away the details of how the authenticated user is stored and retrieved.
 */
@Component
class SecurityContextHolder {

    /**
     * Gets the currently authenticated user, or null if no user is authenticated.
     *
     * @return The current user or null
     */
    fun getCurrentUser(): CurrentUser? {
        // In a real implementation, this would get the user from Spring Security context
        // or another authentication mechanism
        return null
    }
}

/**
 * Represents an authenticated user in the security context.
 */
data class CurrentUser(
    val id: String,
    val username: String,
    val roles: List<String>
)