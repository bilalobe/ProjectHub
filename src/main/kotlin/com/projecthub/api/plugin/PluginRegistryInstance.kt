package com.projecthub.api.plugin

/**
 * Global accessor for the plugin registry.
 * This allows simplified access to plugins from anywhere in the application.
 */
val pluginRegistry: PluginRegistry = PluginRegistry.instance