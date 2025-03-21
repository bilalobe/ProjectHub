package com.projecthub.interfaces.compose.data.adapter

import com.projecthub.interfaces.compose.data.Page
import com.projecthub.interfaces.compose.data.PaginatedRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

/**
 * A cross-platform repository adapter that uses Ktor Client to communicate with REST APIs.
 * Implements the PaginatedRepository interface for consistent usage across all platforms.
 *
 * @param T The entity type this repository manages
 * @param ID The type of the entity's identifier
 * @param httpClient The Ktor HTTP client
 * @param baseUrl The base URL for API requests
 * @param resourcePath The path to the resource endpoint
 * @param json JSON serializer/deserializer
 */
class KtorRestRepository<T : Any, ID>(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val resourcePath: String,
    private val json: Json,
    private val responseClass: Class<T>,
    private val idExtractor: (T) -> ID
) : PaginatedRepository<T, ID> {
    
    private val apiUrl = "$baseUrl/$resourcePath"
    
    override fun getAll(): Flow<List<T>> = flow {
        val response = httpClient.get("$apiUrl/all")
        val entities = response.body<List<T>>()
        emit(entities)
    }
    
    override fun getById(id: ID): Flow<T?> = flow {
        try {
            val response = httpClient.get("$apiUrl/$id")
            val entity = response.body<T>()
            emit(entity)
        } catch (e: Exception) {
            // Handle 404 or other errors
            emit(null)
        }
    }
    
    override suspend fun save(entity: T): T {
        val id = idExtractor(entity)
        
        return if (id != null) {
            // Update existing entity
            val response = httpClient.put("$apiUrl/$id") {
                contentType(ContentType.Application.Json)
                setBody(entity)
            }
            response.body()
        } else {
            // Create new entity
            val response = httpClient.post(apiUrl) {
                contentType(ContentType.Application.Json)
                setBody(entity)
            }
            response.body()
        }
    }
    
    override suspend fun delete(entity: T): Unit {
        val id = idExtractor(entity)
        if (id != null) {
            httpClient.delete("$apiUrl/$id")
        }
    }
    
    override suspend fun deleteById(id: ID) {
        httpClient.delete("$apiUrl/$id")
    }
    
    override fun getPage(page: Int, size: Int): Flow<Page<T>> = flow {
        val response = httpClient.get("$apiUrl?page=$page&size=$size")
        val pageResponse = response.body<PageResponse<T>>()
        
        emit(Page(
            content = pageResponse.content,
            pageNumber = pageResponse.number,
            pageSize = pageResponse.size,
            totalElements = pageResponse.totalElements,
            totalPages = pageResponse.totalPages,
            isFirst = pageResponse.first,
            isLast = pageResponse.last
        ))
    }
    
    override fun search(query: String, page: Int, size: Int): Flow<Page<T>> = flow {
        val response = httpClient.get("$apiUrl/search?query=$query&page=$page&size=$size")
        val pageResponse = response.body<PageResponse<T>>()
        
        emit(Page(
            content = pageResponse.content,
            pageNumber = pageResponse.number,
            pageSize = pageResponse.size,
            totalElements = pageResponse.totalElements,
            totalPages = pageResponse.totalPages,
            isFirst = pageResponse.first,
            isLast = pageResponse.last
        ))
    }
    
    /**
     * Data class matching Spring Data's Page response format
     */
    private data class PageResponse<T>(
        val content: List<T>,
        val number: Int,
        val size: Int,
        val totalElements: Long,
        val totalPages: Int,
        val first: Boolean,
        val last: Boolean
    )
}