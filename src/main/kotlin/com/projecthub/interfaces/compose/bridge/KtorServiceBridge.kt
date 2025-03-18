package com.projecthub.compose.bridge

import com.projecthub.compose.network.HttpClientProvider
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json

/**
 * Ktor implementation of the ServiceBridge that can be used across all platforms.
 * This class provides a uniform way to make API calls using Ktor.
 */
class KtorServiceBridge(
    private val baseUrl: String,
    private val platform: HttpClientProvider.Platform
) : ServiceBridge() {
    
    private val httpClient: HttpClient by lazy {
        HttpClientProvider.getClient(platform, baseUrl)
    }
    
    private val errorHandlers = mutableListOf<(Throwable) -> Unit>()
    
    override suspend fun <T> callService(
        serviceName: String, 
        method: String, 
        params: Map<String, Any?>
    ): Flow<Result<T>> = flow {
        try {
            val endpoint = "$baseUrl/api/$serviceName/$method"
            
            val response: HttpResponse = when (method.lowercase()) {
                "get" -> httpClient.get(endpoint) {
                    params.forEach { (key, value) ->
                        parameter(key, value)
                    }
                }
                "post" -> httpClient.post(endpoint) {
                    contentType(ContentType.Application.Json)
                    setBody(params)
                }
                "put" -> httpClient.put(endpoint) {
                    contentType(ContentType.Application.Json)
                    setBody(params)
                }
                "delete" -> httpClient.delete(endpoint) {
                    params.forEach { (key, value) ->
                        parameter(key, value)
                    }
                }
                else -> throw IllegalArgumentException("Unsupported method: $method")
            }
            
            if (response.status.isSuccess()) {
                val result = response.body<T>()
                emit(Result.success(result))
            } else {
                val errorText = response.bodyAsText()
                throw RuntimeException("Service call failed with status ${response.status}: $errorText")
            }
        } catch (e: Exception) {
            // Notify error handlers
            errorHandlers.forEach { it(e) }
            emit(Result.failure(e))
        }
    }
    
    override fun registerErrorHandler(handler: (Throwable) -> Unit) {
        errorHandlers.add(handler)
    }
    
    /**
     * Releases resources used by this service bridge
     */
    fun release() {
        // The underlying HttpClient will be released by HttpClientProvider
        errorHandlers.clear()
    }
}