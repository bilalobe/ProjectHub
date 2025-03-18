package com.projecthub.compose.network

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.engine.js.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

/**
 * Platform-specific HTTP client configuration.
 * This class handles the creation and configuration of Ktor clients
 * tailored to each platform while maintaining consistent behavior.
 */
object PlatformHttpClient {
    
    /**
     * Shared JSON configuration for all platforms
     */
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
        prettyPrint = false
        encodeDefaults = true
    }
    
    /**
     * Common configuration applied to all clients regardless of platform
     */
    private fun HttpClientConfig<*>.applyCommonConfig() {
        install(ContentNegotiation) {
            json(json)
        }
        
        install(HttpTimeout) {
            connectTimeoutMillis = TimeUnit.SECONDS.toMillis(15)
            requestTimeoutMillis = TimeUnit.MINUTES.toMillis(2)
        }
        
        install(HttpCache)
        
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
        
        install(Resources)
        
        defaultRequest {
            header("X-Client-Platform", "ProjectHub-ComposeUI")
            header("Accept", "application/json")
        }
    }
    
    /**
     * Creates a desktop Ktor client using CIO engine
     * 
     * @param tokenProvider Function that provides the auth token
     * @return Configured HttpClient for desktop platform
     */
    fun createDesktopClient(tokenProvider: suspend () -> String? = { null }): HttpClient {
        return HttpClient(CIO) {
            applyCommonConfig()
            
            engine {
                requestTimeout = TimeUnit.MINUTES.toMillis(2)
                maxConnectionsCount = 100
                endpoint {
                    connectTimeout = TimeUnit.SECONDS.toMillis(15)
                    connectAttempts = 3
                }
            }
            
            // Desktop-specific configuration
            install(Auth) {
                bearer {
                    loadTokens {
                        val token = tokenProvider()
                        if (token != null) {
                            BearerTokens(token, token)
                        } else {
                            null
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Creates an Android Ktor client
     * 
     * @param tokenProvider Function that provides the auth token
     * @return Configured HttpClient for Android platform
     */
    fun createAndroidClient(tokenProvider: suspend () -> String? = { null }): HttpClient {
        return HttpClient(Android) {
            applyCommonConfig()
            
            engine {
                connectTimeout = TimeUnit.SECONDS.toMillis(15)
                socketTimeout = TimeUnit.MINUTES.toMillis(2)
            }
            
            // Android-specific configuration
            install(Auth) {
                bearer {
                    loadTokens {
                        val token = tokenProvider()
                        if (token != null) {
                            BearerTokens(token, token)
                        } else {
                            null
                        }
                    }
                }
            }
            
            // Add auto retry for mobile networks
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 3)
                retryOnException(maxRetries = 3)
                exponentialDelay()
            }
        }
    }
    
    /**
     * Creates a Web Ktor client using JS engine
     * 
     * @param tokenProvider Function that provides the auth token
     * @return Configured HttpClient for web platform
     */
    fun createWebClient(tokenProvider: suspend () -> String? = { null }): HttpClient {
        return HttpClient(Js) {
            applyCommonConfig()
            
            // Web-specific configuration
            install(Auth) {
                bearer {
                    loadTokens {
                        val token = tokenProvider()
                        if (token != null) {
                            BearerTokens(token, token)
                        } else {
                            null
                        }
                    }
                }
            }
            
            // Browser-specific request configuration
            defaultRequest {
                // For web clients, use relative URLs when on the same domain
                header("X-Requested-With", "XMLHttpRequest")
            }
        }
    }
}