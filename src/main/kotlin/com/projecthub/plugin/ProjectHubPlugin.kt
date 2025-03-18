package com.projecthub.plugin

import com.projecthub.plugin.config.PluginConfig
import io.ktor.server.application.*
import io.ktor.server.routing.*

/**
 * Core interface for all ProjectHub plugins
 */
interface ProjectHubPlugin {
    /**
     * Unique identifier for the plugin
     */
    val id: String
    
    /**
     * Human-readable name of the plugin
     */
    val name: String
    
    /**
     * Plugin version
     */
    val version: String
    
    /**
     * Plugin description
     */
    val description: String
    
    /**
     * Initialize the plugin with configuration
     */
    fun initialize(config: PluginConfig = PluginConfig())
    
    /**
     * Configure Ktor routing for this plugin
     */
    fun configureRoutes(routing: Routing)
    
    /**
     * Start the plugin
     */
    fun start()
    
    /**
     * Stop the plugin
     */
    fun stop()
    
    /**
     * Gets required permissions for this plugin
     */
    fun getRequiredPermissions(): List<String> = listOf("plugin:${id}:execute")
}
