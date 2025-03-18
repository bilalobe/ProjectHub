package com.projecthub.security

import org.apache.directory.fortress.core.AccessMgr
import org.apache.directory.fortress.core.ReviewMgr
import org.apache.directory.fortress.core.model.Permission
import org.apache.directory.fortress.core.model.Session

/**
 * Central security hub that integrates Apache Fortress with all application components
 */
class FortressSecurityHub(
    private val accessManager: AccessMgr,
    private val reviewManager: ReviewMgr
) {
    /**
     * Validate plugin access for a given session and plugin ID
     */
    fun validatePluginAccess(session: Session, pluginId: String): Boolean {
        return checkPermission(session, "plugin:$pluginId:use")
    }

    /**
     * General permission checking used across all layers
     */
    fun checkPermission(session: Session, permission: String): Boolean {
        val permObj = Permission(permission)
        permObj.userId = session.userId
        return try {
            val result = reviewManager.checkAccess(session, permObj)
            result != null && result.isAuthorized
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Create a security context for UI integration
     */
    fun createSecurityContext(session: Session): SecurityContext {
        return SecurityContextImpl(session, this)
    }

    /**
     * Inner class that implements the SecurityContext interface
     */
    private inner class SecurityContextImpl(
        private val session: Session,
        private val hub: FortressSecurityHub
    ) : SecurityContext {
        override fun hasPermission(permission: String): Boolean {
            return hub.checkPermission(session, permission)
        }
    }
}

/**
 * Interface for security context used in UI layers
 */
interface SecurityContext {
    fun hasPermission(permission: String): Boolean
}
