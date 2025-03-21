package com.projecthub.plugin

import com.projecthub.session.UserSession

/**
 * Interface for managing plugin lifecycle operations
 * Standardizes how plugins are initialized, started, stopped, and managed
 */
interface PluginLifecycleManager {
    /**
     * Possible states for a plugin
     */
    enum class PluginState {
        DISCOVERED,    // Plugin found but not initialized
        INITIALIZED,   // Plugin initialized but not active
        ACTIVE,        // Plugin running and processing requests
        STOPPED,       // Plugin gracefully stopped
        ERROR,         // Plugin encountered an error
        TERMINATED     // Plugin completely shut down and cleaned up
    }
    
    /**
     * Initialize a plugin
     * 
     * @param plugin The plugin to initialize
     * @param session The user session for security validation
     * @return The result of the initialization
     */
    fun initializePlugin(plugin: ProjectHubPlugin, session: UserSession): LifecycleResult
    
    /**
     * Start a plugin
     * 
     * @param plugin The plugin to start
     * @param session The user session for security validation
     * @return The result of the start operation
     */
    fun startPlugin(plugin: ProjectHubPlugin, session: UserSession): LifecycleResult
    
    /**
     * Stop a plugin
     * 
     * @param plugin The plugin to stop
     * @param session The user session for security validation
     * @return The result of the stop operation
     */
    fun stopPlugin(plugin: ProjectHubPlugin, session: UserSession): LifecycleResult
    
    /**
     * Terminate a plugin, performing any cleanup
     * 
     * @param plugin The plugin to terminate
     * @param session The user session for security validation
     * @return The result of the termination operation
     */
    fun terminatePlugin(plugin: ProjectHubPlugin, session: UserSession): LifecycleResult
    
    /**
     * Get the current state of a plugin
     * 
     * @param pluginId The ID of the plugin
     * @return The current state of the plugin, or null if not found
     */
    fun getPluginState(pluginId: String): PluginState?
    
    /**
     * Result of a lifecycle operation
     */
    data class LifecycleResult(
        val success: Boolean,
        val message: String,
        val newState: PluginState,
        val exception: Exception? = null
    )
}