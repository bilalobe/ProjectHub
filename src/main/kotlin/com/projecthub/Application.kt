package com.projecthub

import com.projecthub.plugin.HybridPluginRegistry
import com.projecthub.security.FortressSecurityAdapter
import com.projecthub.resilience.ResilienceConfig
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.apache.directory.fortress.core.AccessMgrFactory
import org.apache.directory.fortress.core.ReviewMgrFactory

/**
 * Main application entry point
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

/**
 * Application module with all configurations
 */
fun Application.module() {
    // Initialize Fortress security
    val accessManager = AccessMgrFactory.createInstance()
    val reviewManager = ReviewMgrFactory.createInstance()
    val securityHub = FortressSecurityHub(accessManager, reviewManager)

    // Initialize plugin registry with security hub
    val pluginRegistry = HybridPluginRegistry(securityHub)
    pluginRegistry.discoverPlugins()

    // Example session for demonstration
    val session = accessManager.createSession("admin", "password")

    // Validate and start plugins
    pluginRegistry.validateAndStartPlugins(session)

    // Configure Ktor
    configureRouting(pluginRegistry)
    configureSerialization()
    configureSockets()
    configureSecurity(securityHub)

    // Configure metrics
    val meterRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    
    // Initialize Koin with resilience module
    install(Koin) {
        modules(
            // ...existing modules...
            ResilienceConfig.createModule(meterRegistry)
        )
    }
    
    // Add metrics endpoint
    routing {
        get("/metrics") {
            call.respondText(meterRegistry.scrape())
        }
    }
    
    // Register shutdown hook
    environment.monitor.subscribe(ApplicationStopping) {
        log.info("Application stopping, shutting down plugins...")
        pluginRegistry.getPlugins().forEach { plugin ->
            try {
                pluginRegistry.stopPlugin(plugin)
            } catch (e: Exception) {
                log.error("Error stopping plugin ${plugin.id}", e)
            }
        }
    }
}
