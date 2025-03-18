package com.projecthub.interfaces.ui.network

import com.projecthub.interfaces.ui.common.ResourceState
import kotlinx.coroutines.flow.Flow

/**
 * Core API client interface that defines how the UI layer communicates with backend services.
 * This abstraction allows for different implementations (Ktor, Retrofit, etc.) while
 * providing a consistent interface for the UI layer.
 */
interface ApiClient {
    /**
     * Perform a GET request to retrieve a single resource
     *
     * @param endpoint The API endpoint
     * @param params Optional query parameters
     * @return A Flow of ResourceState containing the response or error
     */
    fun <T> get(endpoint: String, params: Map<String, String> = emptyMap()): Flow<ResourceState<T>>

    /**
     * Perform a GET request to retrieve a list of resources
     *
     * @param endpoint The API endpoint
     * @param params Optional query parameters
     * @return A Flow of ResourceState containing the list response or error
     */
    fun <T> getList(endpoint: String, params: Map<String, String> = emptyMap()): Flow<ResourceState<List<T>>>

    /**
     * Perform a POST request to create a resource
     *
     * @param endpoint The API endpoint
     * @param body The request body
     * @return A Flow of ResourceState containing the response or error
     */
    suspend fun <T, R> post(endpoint: String, body: T): ResourceState<R>

    /**
     * Perform a PUT request to update a resource
     *
     * @param endpoint The API endpoint
     * @param body The request body
     * @return A Flow of ResourceState containing the response or error
     */
    suspend fun <T, R> put(endpoint: String, body: T): ResourceState<R>

    /**
     * Perform a DELETE request
     *
     * @param endpoint The API endpoint
     * @return A Flow of ResourceState containing the success status or error
     */
    suspend fun delete(endpoint: String): ResourceState<Boolean>

    /**
     * Perform a search request
     *
     * @param endpoint The API endpoint
     * @param query The search query
     * @return A Flow of ResourceState containing the search results or error
     */
    fun <T> search(endpoint: String, query: String): Flow<ResourceState<List<T>>>
}