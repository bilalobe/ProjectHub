package com.projecthub.interfaces.compose.security

import com.projecthub.security.UnifiedSecurityManager
import com.projecthub.session.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.apache.directory.fortress.core.model.Permission

/**
 * Interface for UI components to check permissions and adapt rendering based on user's access rights.
 * This provides a consistent way for all UI layers (desktop, web, mobile) to perform permission-based rendering.
 */
interface UiPermissionController {
    /**
     * Check if the current user has a specific permission
     * 
     * @param objectName The type of object being accessed
     * @param operation The operation being performed
     * @param objectId Optional specific object identifier
     * @return True if the user has permission, false otherwise
     */
    fun hasPermission(objectName: String, operation: String, objectId: String? = null): Boolean
    
    /**
     * Check if the current user has a specific role
     * 
     * @param roleName The role to check
     * @return True if the user has the role, false otherwise
     */
    fun hasRole(roleName: String): Boolean
    
    /**
     * Get all permissions for the current user.
     * Useful for building complex UI permission models.
     * 
     * @return List of all permissions the user has
     */
    fun getAllPermissions(): List<Permission>
    
    /**
     * Observe changes to the current session.
     * UI components can subscribe to this flow to update when permissions change.
     * 
     * @return Flow of UserSession changes
     */
    fun observeSession(): Flow<UserSession?>
    
    /**
     * Update the current session.
     * Should be called when user logs in or out.
     * 
     * @param session The new session or null if logged out
     */
    fun updateSession(session: UserSession?)
}

/**
 * Implementation of UiPermissionController that delegates to UnifiedSecurityManager
 */
class SecurityHubUiPermissionController(
    private val securityManager: UnifiedSecurityManager
) : UiPermissionController {
    
    private val sessionFlow = MutableStateFlow<UserSession?>(null)
    
    override fun hasPermission(objectName: String, operation: String, objectId: String?): Boolean {
        val session = sessionFlow.value ?: return false
        return securityManager.checkPermission(session, objectName, operation, objectId)
    }
    
    override fun hasRole(roleName: String): Boolean {
        val session = sessionFlow.value ?: return false
        return securityManager.hasRole(session, roleName)
    }
    
    override fun getAllPermissions(): List<Permission> {
        val session = sessionFlow.value ?: return emptyList()
        return securityManager.getAllPermissions(session)
    }
    
    override fun observeSession(): Flow<UserSession?> = sessionFlow
    
    override fun updateSession(session: UserSession?) {
        // If session is changing to null, clear any cached permissions
        if (session == null && sessionFlow.value != null) {
            sessionFlow.value?.sessionId?.let { securityManager.invalidateSessionCache(it) }
        }
        
        sessionFlow.value = session
    }
}