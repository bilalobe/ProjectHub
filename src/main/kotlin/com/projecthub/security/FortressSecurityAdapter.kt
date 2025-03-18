package com.projecthub.security

import com.projecthub.session.UserSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.plugins.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import org.apache.directory.fortress.core.AccessMgr
import org.apache.directory.fortress.core.ReviewMgr
import org.apache.directory.fortress.core.model.Permission
import org.apache.directory.fortress.core.model.User
import org.slf4j.LoggerFactory

/**
 * Bridge between Ktor authentication and Apache Fortress RBAC
 */
class FortressSecurityAdapter(
    private val accessManager: AccessMgr,
    private val reviewMgr: ReviewMgr
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    /**
     * Check if the user has the specified permission
     */
    fun checkPermission(session: UserSession, permission: String): Boolean {
        val userId = session.userId
        
        // Fortress permission check
        return try {
            val permObj = Permission(permission)
            permObj.userId = userId
            val result = reviewMgr.checkAccess(permObj)
            result != null && result.isAuthorized
        } catch (e: Exception) {
            logger.error("Error checking permission $permission for user $userId", e)
            false
        }
    }
    
    /**
     * Create a user session from a Fortress user
     */
    fun createSession(user: User): UserSession {
        return UserSession(
            userId = user.userId,
            roles = user.roles?.map { it.name } ?: emptyList()
        )
    }
    
    /**
     * Create a Ktor authentication plugin that integrates with Fortress
     */
    fun ktorAuthProvider() = createApplicationPlugin("FortressAuth") {
        onCall { call ->
            val session = call.sessions.get<UserSession>() ?: throw UnauthorizedException()
            val requiredPermission = call.attributes.getOrNull(AttributeKey<String>("permission"))
            if (requiredPermission != null && !checkPermission(session, requiredPermission)) {
                throw ForbiddenException("Insufficient permissions")
            }
        }
    }
    
    /**
     * Get the required permission for a route
     * This can be expanded to use annotations or a configuration file
     */
    private fun getRequiredPermissionForRoute(route: String): String {
        // Example mapping of routes to permissions
        return when {
            route.startsWith("/api/admin") -> "admin:access"
            route.startsWith("/api/projects") -> "projects:access"
            route.startsWith("/api/plugins") -> "plugins:access"
            else -> ""
        }
    }
    
    /**
     * Permission annotation for routes and controllers
     */
    @Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RequiresPermission(val permission: String)
}
