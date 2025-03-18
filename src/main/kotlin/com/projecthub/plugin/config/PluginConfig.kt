package com.projecthub.plugin.config

/**
 * Configuration class for plugins
 */
class PluginConfig {
    private val properties = mutableMapOf<String, Any?>()
    
    fun <T> getProperty(key: String, defaultValue: T): T {
        @Suppress("UNCHECKED_CAST")
        return properties.getOrDefault(key, defaultValue) as T
    }
    
    fun <T> setProperty(key: String, value: T) {
        properties[key] = value
    }
    
    fun hasProperty(key: String): Boolean = properties.containsKey(key)
}
