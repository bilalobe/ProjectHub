package com.projecthub.plugin

/**
 * Registry for managing ProjectHub plugins
 */
interface PluginRegistry {
    /**
     * Get all registered plugins
     */
    fun getPlugins(): List<ProjectHubPlugin>
    
    /**
     * Get plugin by ID
     */
    fun getPlugin(id: String): ProjectHubPlugin?
    
    /**
     * Register a plugin
     */
    fun registerPlugin(plugin: ProjectHubPlugin)
}
