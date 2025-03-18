package com.projecthub.interfaces.ui.network

import com.projecthub.interfaces.ui.common.ResourceState
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

/**
 * Implementation of ApiClient using Ktor HTTP client
 */
class KtorApiClient(
    private val baseUrl: String,
    private val tokenProvider: () -> String? = { null },
    private val httpClient: HttpClient = createDefaultHttpClient()
) : ApiClient {

    override fun <T> get(endpoint: String, params: Map<String, String>): Flow<ResourceState<T>> = flow {
        emit(ResourceState.Loading)
        try {
            val response = httpClient.get("$baseUrl$endpoint") {
                params.forEach { (key, value) ->
                    parameter(key, value)
                }
                addAuthHeader()
            }
            val result: T = response.body()
            emit(ResourceState.Success(result))
        } catch (e: Exception) {
            emit(ResourceState.Error(e))
        }
    }

    override fun <T> getList(endpoint: String, params: Map<String, String>): Flow<ResourceState<List<T>>> = flow {
        emit(ResourceState.Loading)
        try {
            val response = httpClient.get("$baseUrl$endpoint") {
                params.forEach { (key, value) ->
                    parameter(key, value)
                }
                addAuthHeader()
            }
            val result: List<T> = response.body()
            emit(ResourceState.Success(result))
        } catch (e: Exception) {
            emit(ResourceState.Error(e))
        }
    }

    override suspend fun <T, R> post(endpoint: String, body: T): ResourceState<R> {
        return try {
            val response = httpClient.post("$baseUrl$endpoint") {
                contentType(ContentType.Application.Json)
                setBody(body)
                addAuthHeader()
            }
            val result: R = response.body()
            ResourceState.Success(result)
        } catch (e: Exception) {
            ResourceState.Error(e)
        }
    }

    override suspend fun <T, R> put(endpoint: String, body: T): ResourceState<R> {
        return try {
            val response = httpClient.put("$baseUrl$endpoint") {
                contentType(ContentType.Application.Json)
                setBody(body)
                addAuthHeader()
            }
            val result: R = response.body()
            ResourceState.Success(result)
        } catch (e: Exception) {
            ResourceState.Error(e)
        }
    }

    override suspend fun delete(endpoint: String): ResourceState<Boolean> {
        return try {
            httpClient.delete("$baseUrl$endpoint") {
                addAuthHeader()
            }
            ResourceState.Success(true)
        } catch (e: Exception) {
            ResourceState.Error(e)
        }
    }

    override fun <T> search(endpoint: String, query: String): Flow<ResourceState<List<T>>> = flow {
        emit(ResourceState.Loading)
        try {
            val response = httpClient.get("$baseUrl$endpoint") {
                parameter("q", query)
                addAuthHeader()
            }
            val result: List<T> = response.body()
            emit(ResourceState.Success(result))
        } catch (e: Exception) {
            emit(ResourceState.Error(e))
        }
    }

    private fun HttpRequestBuilder.addAuthHeader() {
        tokenProvider()?.let { token ->
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }

    companion object {
        fun createDefaultHttpClient() = HttpClient(CIO) {
            install(ContentNegotiation) {
                jackson()
            }
            
            install(Logging) {
                level = LogLevel.INFO
            }
            
            install(HttpTimeout) {
                connectTimeoutMillis = TimeUnit.SECONDS.toMillis(10)
                requestTimeoutMillis = TimeUnit.SECONDS.toMillis(30)
            }
            
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 3)
                exponentialDelay()
            }
            
            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }
        }
    }
}