package com.projecthub.security

import com.projecthub.session.UserSession
import org.apache.directory.fortress.core.AccessMgr
import org.apache.directory.fortress.core.ReviewMgr
import org.apache.directory.fortress.core.DelAccessMgr
import org.apache.directory.fortress.core.AdminMgr
import org.apache.directory.fortress.core.model.Permission
import org.apache.directory.fortress.core.model.Session
import org.slf4j.LoggerFactory

/**
 * Centralized security manager that implements a unified security policy
 * across all types of routes: REST, WebSocket, SSE, and plugin routes.
 * 
 * This class integrates advanced Fortress features and provides a consistent
 * permission-checking mechanism regardless of the communication channel.
 */
class UnifiedSecurityManager(
    private val accessManager: AccessMgr,
    private val reviewManager: ReviewMgr,
    private val delAccessManager: DelAccessMgr,
    private val adminManager: AdminMgr
) {
    private val logger = LoggerFactory.getLogger(UnifiedSecurityManager::class.java)
    
    // Cache for performance optimization - stores permissions by session ID
    private val permissionCache = mutableMapOf<String, MutableSet<String>>()
    
    /**
     * Check if a user has a specific permission
     */
    fun checkPermission(session: UserSession, objectName: String, operation: String, objectId: String? = null): Boolean {
        val cacheKey = "${session.sessionId}:${objectName}:${operation}:${objectId ?: "*"}"
        
        // Check cache first for performance
        if (permissionCache.containsKey(session.sessionId)) {
            val permissions = permissionCache[session.sessionId]
            if (permissions?.contains(cacheKey) == true) {
                return true
            }
        }
        
        // Convert UserSession to Fortress Session
        val fortressSession = Session(session.sessionId)
        fortressSession.userId = session.userId
        
        // Create Fortress Permission
        val permission = Permission(objectName, operation)
        if (objectId != null) {
            permission.objId = objectId
        }
        
        try {
            // Check permission with Fortress
            val hasPermission = accessManager.checkAccess(fortressSession, permission)
            
            // If permission granted, cache it
            if (hasPermission) {
                if (!permissionCache.containsKey(session.sessionId)) {
                    permissionCache[session.sessionId] = mutableSetOf()
                }
                permissionCache[session.sessionId]?.add(cacheKey)
            }
            
            return hasPermission
        } catch (e: Exception) {
            logger.error("Error checking permission: $objectName:$operation:${objectId ?: "*"}", e)
            return false
        }
    }
    
    /**
     * Check if a user has a role
     */
    fun hasRole(session: UserSession, roleName: String): Boolean {
        try {
            val fortressSession = Session(session.sessionId)
            fortressSession.userId = session.userId
            
            // Use the review manager to check roles
            val userRoles = reviewManager.readUser(session.userId).roles
            return userRoles.any { it.name.equals(roleName, ignoreCase = true) }
        } catch (e: Exception) {
            logger.error("Error checking role: $roleName", e)
            return false
        }
    }
    
    /**
     * Check delegated administrative permissions
     * This is an advanced Fortress feature for delegation
     */
    fun checkDelegatedPermission(session: UserSession, objectName: String, operation: String, objectId: String? = null): Boolean {
        try {
            val fortressSession = Session(session.sessionId)
            fortressSession.userId = session.userId
            
            // Create Fortress Permission
            val permission = Permission(objectName, operation)
            if (objectId != null) {
                permission.objId = objectId
            }
            
            // Check delegated permissions
            return delAccessManager.checkAccess(fortressSession, permission)
        } catch (e: Exception) {
            logger.error("Error checking delegated permission: $objectName:$operation:${objectId ?: "*"}", e)
            return false
        }
    }
    
    /**
     * Invalidate cache for a session when it's no longer needed
     */
    fun invalidateSessionCache(sessionId: String) {
        permissionCache.remove(sessionId)
    }
    
    /**
     * Get all permissions for a user
     * Useful for UI permission-based rendering
     */
    fun getAllPermissions(session: UserSession): List<Permission> {
        try {
            val fortressSession = Session(session.sessionId)
            fortressSession.userId = session.userId
            
            // Use the review manager to get all permissions
            return reviewManager.readPermissions(fortressSession)
        } catch (e: Exception) {
            logger.error("Error fetching all permissions for user: ${session.userId}", e)
            return emptyList()
        }
    }
    
    /**
     * Create a new administrative permission
     * Used for dynamic permission management
     */
    fun createPermission(session: UserSession, objectName: String, operation: String, objectId: String? = null): Boolean {
        try {
            // Verify the user has admin rights first
            if (!checkPermission(session, "permission", "create", null)) {
                logger.warn("User ${session.userId} attempted to create permission without authorization")
                return false
            }
            
            // Create the new permission
            val permission = Permission(objectName, operation)
            if (objectId != null) {
                permission.objId = objectId
            }
            
            adminManager.addPermission(permission)
            return true
        } catch (e: Exception) {
            logger.error("Error creating permission: $objectName:$operation:${objectId ?: "*"}", e)
            return false
        }
    }
    
    /**
     * Assign a permission to a role
     * Used for dynamic role management
     */
    fun assignPermissionToRole(session: UserSession, roleName: String, objectName: String, 
                              operation: String, objectId: String? = null): Boolean {
        try {
            // Verify the user has admin rights first
            if (!checkPermission(session, "role", "assign", null)) {
                logger.warn("User ${session.userId} attempted to assign permission without authorization")
                return false
            }
            
            // Create the permission
            val permission = Permission(objectName, operation)
            if (objectId != null) {
                permission.objId = objectId
            }
            permission.roles = setOf(roleName)
            
            // Assign the permission to the role
            adminManager.addPermissionToRole(permission, roleName)
            return true
        } catch (e: Exception) {
            logger.error("Error assigning permission to role: $roleName -> $objectName:$operation:${objectId ?: "*"}", e)
            return false
        }
    }
}