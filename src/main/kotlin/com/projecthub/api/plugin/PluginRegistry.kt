package com.projecthub.api.plugin

import org.slf4j.LoggerFactory

/**
 * Registry for managing plugins that can integrate with the Ktor API
 */
class PluginRegistry {
    private val logger = LoggerFactory.getLogger(PluginRegistry::class.java)
    private val plugins = mutableMapOf<String, Any>()

    /**
     * Register a new plugin
     *
     * @param plugin The plugin to register
     * @throws IllegalArgumentException if a plugin with the same ID already exists
     */
    fun register(plugin: KtorCapablePlugin) {
        val id = plugin.id
        if (plugins.containsKey(id)) {
            throw IllegalArgumentException("Plugin with ID '\' is already registered")
        }

        plugins[id] = plugin
        logger.info("Registered plugin: \")
    }

    /**
     * Get a plugin by ID
     *
     * @param id The plugin ID
     * @return The plugin or null if not found
     */
    fun getPlugin(id: String): Any? = plugins[id]

    /**
     * Get all plugins of a specific type
     *
     * @param T The plugin type to filter by
     * @return A list of plugins matching the specified type
     */
    inline fun <reified T> getPlugins(): List<T> {
        return plugins.values.filterIsInstance<T>()
    }

    /**
     * Get all registered plugins
     *
     * @return All registered plugins
     */
    fun getAllPlugins(): Collection<Any> = plugins.values

    /**
     * Check if a plugin with the given ID exists
     *
     * @param id The plugin ID
     * @return true if the plugin exists, false otherwise
     */
    fun hasPlugin(id: String): Boolean = plugins.containsKey(id)

    /**
     * Remove a plugin by ID
     *
     * @param id The plugin ID
     * @return true if the plugin was removed, false if it didn't exist
     */
    fun unregister(id: String): Boolean {
        val removed = plugins.remove(id) != null
        if (removed) {
            logger.info("Unregistered plugin: \")
        }
        return removed
    }

    /**
     * Clear all registered plugins
     */
    fun clear() {
        plugins.clear()
        logger.info("Cleared all plugins")
    }

    companion object {
        // Singleton instance
        val instance by lazy { PluginRegistry() }
    }
}

/**
 * Global accessor for the plugin registry.
 * This allows simplified access to plugins from anywhere in the application.
 */
val pluginRegistry: PluginRegistry = PluginRegistry.instance
