package com.projecthub.security

import com.projecthub.plugin.ProjectHubPlugin
import com.projecthub.session.UserSession
import org.slf4j.LoggerFactory

/**
 * Security service specifically for plugin management
 */
class PluginSecurityService(private val securityAdapter: FortressSecurityAdapter) {
    private val logger = LoggerFactory.getLogger(PluginSecurityService::class.java)
    
    /**
     * Validate if a user can access a plugin
     */
    fun validatePluginAccess(plugin: ProjectHubPlugin, session: UserSession): Boolean {
        val pluginPermission = "plugin:${plugin.id}:access"
        return securityAdapter.checkPermission(session, pluginPermission)
    }
    
    /**
     * Validate if a user can execute a plugin operation
     */
    fun validatePluginOperation(plugin: ProjectHubPlugin, operation: String, session: UserSession): Boolean {
        val pluginPermission = "plugin:${plugin.id}:$operation"
        val result = securityAdapter.checkPermission(session, pluginPermission)
        
        if (!result) {
            logger.warn("User ${session.userId} attempted unauthorized operation $operation on plugin ${plugin.id}")
        }
        
        return result
    }
    
    /**
     * Get all plugins that a user has access to
     */
    fun filterAccessiblePlugins(plugins: List<ProjectHubPlugin>, session: UserSession): List<ProjectHubPlugin> {
        return plugins.filter { plugin ->
            validatePluginAccess(plugin, session)
        }
    }
}
