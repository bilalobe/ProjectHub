package com.projecthub.compose.network

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Provider for platform-specific HTTP clients
 */
object HttpClientProvider {
    // Shared JSON configuration
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }
    
    // Platform type for configuring the appropriate client
    enum class Platform {
        DESKTOP, ANDROID, WEB
    }
    
    // Client instances by platform
    private val clients = mutableMapOf<Platform, HttpClient>()
    
    /**
     * Returns a platform-specific HTTP client or creates a new one if none exists
     */
    fun getClient(platform: Platform, baseUrl: String): HttpClient {
        return clients.getOrPut(platform) { createClient(platform, baseUrl) }
    }
    
    /**
     * Creates a new HTTP client for the given platform with appropriate configuration
     */
    private fun createClient(platform: Platform, baseUrl: String): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(json)
            }
            
            // Common client configuration
            expectSuccess = true
            
            // Platform-specific configuration would go here
            when(platform) {
                Platform.DESKTOP -> {
                    // Desktop-specific configuration
                }
                Platform.ANDROID -> {
                    // Android-specific configuration
                }
                Platform.WEB -> {
                    // Web-specific configuration
                }
            }
        }
    }
    
    /**
     * Releases all clients and their resources
     */
    fun release() {
        clients.values.forEach { it.close() }
        clients.clear()
    }
}