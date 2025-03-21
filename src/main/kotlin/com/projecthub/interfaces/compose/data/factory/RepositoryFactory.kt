package com.projecthub.interfaces.compose.data.factory

import com.projecthub.interfaces.compose.data.Repository
import com.projecthub.interfaces.compose.data.PaginatedRepository
import com.projecthub.interfaces.compose.data.security.SecureEntity
import com.projecthub.interfaces.compose.data.security.SecurePaginatedRepositoryDecorator
import com.projecthub.interfaces.compose.data.security.SecureRepositoryDecorator
import com.projecthub.interfaces.compose.security.UiPermissionController
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

/**
 * Factory class for creating repositories with security decorators.
 * This creates a consistent pattern for repository creation across all platforms.
 */
class RepositoryFactory(
    private val permissionController: UiPermissionController,
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val json: Json
) {
    /**
     * Create a secure repository for a given entity type
     *
     * @param resourcePath The API path for the resource
     * @param responseClass Class of the entity type
     * @param idExtractor Function to extract the ID from an entity
     * @return A secure repository instance
     */
    fun <T, ID> createSecureRepository(
        resourcePath: String,
        responseClass: Class<T>,
        idExtractor: (T) -> ID
    ): Repository<T, ID> where T : SecureEntity, T : Any {
        // Create the base repository
        val baseRepository = KtorRestRepository<T, ID>(
            httpClient = httpClient,
            baseUrl = baseUrl,
            resourcePath = resourcePath,
            json = json,
            responseClass = responseClass,
            idExtractor = idExtractor
        )
        
        // Wrap it with the security decorator
        return SecureRepositoryDecorator(
            delegate = baseRepository,
            permissionController = permissionController
        )
    }
    
    /**
     * Create a secure paginated repository for a given entity type
     *
     * @param resourcePath The API path for the resource
     * @param responseClass Class of the entity type
     * @param idExtractor Function to extract the ID from an entity
     * @return A secure paginated repository instance
     */
    fun <T, ID> createSecurePaginatedRepository(
        resourcePath: String,
        responseClass: Class<T>,
        idExtractor: (T) -> ID
    ): PaginatedRepository<T, ID> where T : SecureEntity, T : Any {
        // Create the base paginated repository
        val baseRepository = KtorRestRepository<T, ID>(
            httpClient = httpClient,
            baseUrl = baseUrl,
            resourcePath = resourcePath,
            json = json,
            responseClass = responseClass,
            idExtractor = idExtractor
        )
        
        // Wrap it with the security decorator
        return SecurePaginatedRepositoryDecorator(
            delegate = baseRepository,
            permissionController = permissionController
        )
    }
}