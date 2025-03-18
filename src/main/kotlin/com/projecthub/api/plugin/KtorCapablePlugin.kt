package com.projecthub.api.plugin

import io.ktor.server.routing.*

/**
 * Interface for plugins that want to register their own Ktor routes.
 * This allows existing ProjectHub plugins to expose functionality through
 * the Ktor API without direct dependencies.
 */
interface KtorCapablePlugin {
    /**
     * The unique identifier for this plugin.
     * Will be used as part of the route path: /plugins/{id}/...
     */
    val id: String

    /**
     * Register routes for this plugin.
     * This is called with the plugin's Route scope, allowing each plugin
     * to define its own endpoints.
     */
    fun Route.registerRoutes()
}
