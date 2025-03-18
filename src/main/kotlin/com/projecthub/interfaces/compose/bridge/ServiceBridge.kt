package com.projecthub.compose.bridge

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Abstract service bridge that provides a platform-independent way for UI components
 * to communicate with backend services. Each platform will implement this interface
 * according to their specific needs.
 * 
 * This allows us to keep the UI components platform-independent while still being
 * able to communicate with backend services.
 */
abstract class ServiceBridge {
    /**
     * Generic method to call a backend service
     * 
     * @param serviceName The name of the service to call
     * @param method The method to call on the service
     * @param params Parameters to pass to the service method
     * @return A flow of the result
     */
    abstract suspend fun <T> callService(serviceName: String, method: String, params: Map<String, Any?>): Flow<Result<T>>
    
    /**
     * Registers error handlers for service calls
     * 
     * @param handler The error handler to register
     */
    abstract fun registerErrorHandler(handler: (Throwable) -> Unit)
    
    companion object {
        // Holds the current implementation of the service bridge
        private val instance = MutableStateFlow<ServiceBridge?>(null)
        
        /**
         * Sets the service bridge implementation
         */
        fun setInstance(bridge: ServiceBridge) {
            instance.value = bridge
        }
        
        /**
         * Gets the current service bridge implementation
         */
        fun getInstance(): ServiceBridge {
            return instance.value ?: throw IllegalStateException("ServiceBridge not initialized")
        }
        
        /**
         * Observes the service bridge instance
         */
        fun observeInstance(): StateFlow<ServiceBridge?> = instance.asStateFlow()
    }
}