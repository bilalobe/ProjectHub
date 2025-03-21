package com.projecthub.plugin

import com.projecthub.security.PluginSecurityService
import com.projecthub.session.UserSession
import org.slf4j.LoggerFactory

/**
 * Standard implementation of PluginLifecycleManager that integrates security
 * controls with plugin lifecycle operations
 */
class SecurePluginLifecycleManager(
    private val securityService: PluginSecurityService
) : PluginLifecycleManager {
    private val logger = LoggerFactory.getLogger(SecurePluginLifecycleManager::class.java)
    private val pluginStates = mutableMapOf<String, PluginLifecycleManager.PluginState>()
    
    override fun initializePlugin(
        plugin: ProjectHubPlugin, 
        session: UserSession
    ): PluginLifecycleManager.LifecycleResult {
        // Check permission for initializing a plugin
        if (!securityService.validatePluginOperation(plugin, "initialize", session)) {
            logger.warn("User ${session.userId} denied permission to initialize plugin ${plugin.id}")
            return PluginLifecycleManager.LifecycleResult(
                success = false,
                message = "Permission denied: initialize operation",
                newState = PluginLifecycleManager.PluginState.DISCOVERED
            )
        }
        
        return try {
            logger.info("Initializing plugin ${plugin.id}")
            plugin.initialize()
            pluginStates[plugin.id] = PluginLifecycleManager.PluginState.INITIALIZED
            PluginLifecycleManager.LifecycleResult(
                success = true,
                message = "Plugin initialized successfully",
                newState = PluginLifecycleManager.PluginState.INITIALIZED
            )
        } catch (e: Exception) {
            logger.error("Failed to initialize plugin ${plugin.id}", e)
            pluginStates[plugin.id] = PluginLifecycleManager.PluginState.ERROR
            PluginLifecycleManager.LifecycleResult(
                success = false,
                message = "Failed to initialize plugin: ${e.message}",
                newState = PluginLifecycleManager.PluginState.ERROR,
                exception = e
            )
        }
    }
    
    override fun startPlugin(
        plugin: ProjectHubPlugin, 
        session: UserSession
    ): PluginLifecycleManager.LifecycleResult {
        // Check current state
        val currentState = pluginStates[plugin.id]
        if (currentState != PluginLifecycleManager.PluginState.INITIALIZED) {
            return PluginLifecycleManager.LifecycleResult(
                success = false,
                message = "Plugin must be in INITIALIZED state to start, current state: $currentState",
                newState = currentState ?: PluginLifecycleManager.PluginState.ERROR
            )
        }
        
        // Check permission for starting a plugin
        if (!securityService.validatePluginOperation(plugin, "start", session)) {
            logger.warn("User ${session.userId} denied permission to start plugin ${plugin.id}")
            return PluginLifecycleManager.LifecycleResult(
                success = false,
                message = "Permission denied: start operation",
                newState = currentState
            )
        }
        
        return try {
            logger.info("Starting plugin ${plugin.id}")
            plugin.start()
            pluginStates[plugin.id] = PluginLifecycleManager.PluginState.ACTIVE
            PluginLifecycleManager.LifecycleResult(
                success = true,
                message = "Plugin started successfully",
                newState = PluginLifecycleManager.PluginState.ACTIVE
            )
        } catch (e: Exception) {
            logger.error("Failed to start plugin ${plugin.id}", e)
            pluginStates[plugin.id] = PluginLifecycleManager.PluginState.ERROR
            PluginLifecycleManager.LifecycleResult(
                success = false,
                message = "Failed to start plugin: ${e.message}",
                newState = PluginLifecycleManager.PluginState.ERROR,
                exception = e
            )
        }
    }
    
    override fun stopPlugin(
        plugin: ProjectHubPlugin, 
        session: UserSession
    ): PluginLifecycleManager.LifecycleResult {
        // Check current state
        val currentState = pluginStates[plugin.id]
        if (currentState != PluginLifecycleManager.PluginState.ACTIVE) {
            return PluginLifecycleManager.LifecycleResult(
                success = false,
                message = "Plugin must be in ACTIVE state to stop, current state: $currentState",
                newState = currentState ?: PluginLifecycleManager.PluginState.ERROR
            )
        }
        
        // Check permission for stopping a plugin
        if (!securityService.validatePluginOperation(plugin, "stop", session)) {
            logger.warn("User ${session.userId} denied permission to stop plugin ${plugin.id}")
            return PluginLifecycleManager.LifecycleResult(
                success = false,
                message = "Permission denied: stop operation",
                newState = currentState
            )
        }
        
        return try {
            logger.info("Stopping plugin ${plugin.id}")
            plugin.stop()
            pluginStates[plugin.id] = PluginLifecycleManager.PluginState.STOPPED
            PluginLifecycleManager.LifecycleResult(
                success = true,
                message = "Plugin stopped successfully",
                newState = PluginLifecycleManager.PluginState.STOPPED
            )
        } catch (e: Exception) {
            logger.error("Failed to stop plugin ${plugin.id}", e)
            pluginStates[plugin.id] = PluginLifecycleManager.PluginState.ERROR
            PluginLifecycleManager.LifecycleResult(
                success = false,
                message = "Failed to stop plugin: ${e.message}",
                newState = PluginLifecycleManager.PluginState.ERROR,
                exception = e
            )
        }
    }
    
    override fun terminatePlugin(
        plugin: ProjectHubPlugin, 
        session: UserSession
    ): PluginLifecycleManager.LifecycleResult {
        // Get current state (can terminate from any state except DISCOVERED)
        val currentState = pluginStates[plugin.id]
        if (currentState == PluginLifecycleManager.PluginState.DISCOVERED) {
            return PluginLifecycleManager.LifecycleResult(
                success = false,
                message = "Cannot terminate a plugin that is only discovered but not initialized",
                newState = currentState ?: PluginLifecycleManager.PluginState.ERROR
            )
        }
        
        // Check permission for terminating a plugin
        if (!securityService.validatePluginOperation(plugin, "terminate", session)) {
            logger.warn("User ${session.userId} denied permission to terminate plugin ${plugin.id}")
            return PluginLifecycleManager.LifecycleResult(
                success = false,
                message = "Permission denied: terminate operation",
                newState = currentState ?: PluginLifecycleManager.PluginState.ERROR
            )
        }
        
        // If plugin is active, stop it first
        if (currentState == PluginLifecycleManager.PluginState.ACTIVE) {
            try {
                plugin.stop()
            } catch (e: Exception) {
                logger.warn("Error stopping plugin ${plugin.id} during termination", e)
                // Continue with termination despite stop error
            }
        }
        
        return try {
            logger.info("Terminating plugin ${plugin.id}")
            // Use the plugin's terminate method now that it's defined in the interface
            plugin.terminate()
            
            pluginStates[plugin.id] = PluginLifecycleManager.PluginState.TERMINATED
            PluginLifecycleManager.LifecycleResult(
                success = true,
                message = "Plugin terminated successfully",
                newState = PluginLifecycleManager.PluginState.TERMINATED
            )
        } catch (e: Exception) {
            logger.error("Failed to terminate plugin ${plugin.id}", e)
            pluginStates[plugin.id] = PluginLifecycleManager.PluginState.ERROR
            PluginLifecycleManager.LifecycleResult(
                success = false,
                message = "Failed to terminate plugin: ${e.message}",
                newState = PluginLifecycleManager.PluginState.ERROR,
                exception = e
            )
        }
    }
    
    override fun getPluginState(pluginId: String): PluginLifecycleManager.PluginState? {
        return pluginStates[pluginId]
    }
    
    /**
     * Register a discovered plugin
     */
    fun registerPlugin(plugin: ProjectHubPlugin) {
        pluginStates[plugin.id] = PluginLifecycleManager.PluginState.DISCOVERED
        logger.info("Registered plugin ${plugin.id} in DISCOVERED state")
    }
    
    /**
     * Get all plugins in a specific state
     */
    fun getPluginsByState(state: PluginLifecycleManager.PluginState): List<String> {
        return pluginStates.filterValues { it == state }.keys.toList()
    }
    
    /**
     * Get all plugin states
     */
    fun getAllPluginStates(): Map<String, PluginLifecycleManager.PluginState> {
        return pluginStates.toMap()
    }
}