package com.projecthub.interfaces.compose.data.security

import com.projecthub.interfaces.compose.data.Page
import com.projecthub.interfaces.compose.data.PaginatedRepository
import com.projecthub.interfaces.compose.data.Repository
import com.projecthub.interfaces.compose.security.UiPermissionController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Interface for entities with permission-based access control
 */
interface SecureEntity {
    /**
     * Get the object name for permission checks
     */
    fun getPermissionObjectName(): String
    
    /**
     * Get the object ID for permission checks
     */
    fun getPermissionObjectId(): String
    
    /**
     * Check if this entity requires specific access permissions
     */
    fun requiresSpecificPermission(): Boolean = true
}

/**
 * Secure repository decorator that applies permission filtering to repository operations.
 * This follows the Decorator pattern to add security checks to any Repository implementation.
 *
 * @param T The entity type this repository manages (must implement SecureEntity)
 * @param ID The type of the entity's identifier
 * @param delegate The underlying repository implementation
 * @param permissionController The controller for permission checks
 */
class SecureRepositoryDecorator<T : SecureEntity, ID>(
    private val delegate: Repository<T, ID>,
    private val permissionController: UiPermissionController
) : Repository<T, ID> {
    
    override fun getAll(): Flow<List<T>> {
        return delegate.getAll().map { entities ->
            entities.filter { entity -> hasReadPermission(entity) }
        }
    }
    
    override fun getById(id: ID): Flow<T?> {
        return delegate.getById(id).map { entity ->
            if (entity != null && hasReadPermission(entity)) entity else null
        }
    }
    
    override suspend fun save(entity: T): T {
        // Check write permission before saving
        if (!hasWritePermission(entity)) {
            throw SecurityException("Permission denied: cannot save entity")
        }
        return delegate.save(entity)
    }
    
    override suspend fun delete(entity: T): Unit {
        // Check delete permission before deleting
        if (!hasDeletePermission(entity)) {
            throw SecurityException("Permission denied: cannot delete entity")
        }
        delegate.delete(entity)
    }
    
    override suspend fun deleteById(id: ID) {
        // Get the entity first to check permissions
        val entity = delegate.getById(id).map { it }.also { flow ->
            val value = flow
            if (value != null && !hasDeletePermission(value)) {
                throw SecurityException("Permission denied: cannot delete entity")
            }
        }
        
        delegate.deleteById(id)
    }
    
    private fun hasReadPermission(entity: T): Boolean {
        val objectName = entity.getPermissionObjectName()
        val objectId = if (entity.requiresSpecificPermission()) entity.getPermissionObjectId() else null
        return permissionController.hasPermission(objectName, "read", objectId)
    }
    
    private fun hasWritePermission(entity: T): Boolean {
        val objectName = entity.getPermissionObjectName()
        val objectId = if (entity.requiresSpecificPermission()) entity.getPermissionObjectId() else null
        return permissionController.hasPermission(objectName, "update", objectId)
    }
    
    private fun hasDeletePermission(entity: T): Boolean {
        val objectName = entity.getPermissionObjectName()
        val objectId = if (entity.requiresSpecificPermission()) entity.getPermissionObjectId() else null
        return permissionController.hasPermission(objectName, "delete", objectId)
    }
}

/**
 * Secure paginated repository decorator that applies permission filtering to paginated repository operations.
 *
 * @param T The entity type this repository manages (must implement SecureEntity)
 * @param ID The type of the entity's identifier
 * @param delegate The underlying paginated repository implementation
 * @param permissionController The controller for permission checks
 */
class SecurePaginatedRepositoryDecorator<T : SecureEntity, ID>(
    private val delegate: PaginatedRepository<T, ID>,
    private val permissionController: UiPermissionController
) : PaginatedRepository<T, ID> {
    
    private val secureDelegate = SecureRepositoryDecorator(delegate, permissionController)
    
    override fun getAll(): Flow<List<T>> = secureDelegate.getAll()
    
    override fun getById(id: ID): Flow<T?> = secureDelegate.getById(id)
    
    override suspend fun save(entity: T): T = secureDelegate.save(entity)
    
    override suspend fun delete(entity: T) = secureDelegate.delete(entity)
    
    override suspend fun deleteById(id: ID) = secureDelegate.deleteById(id)
    
    override fun getPage(page: Int, size: Int): Flow<Page<T>> {
        return delegate.getPage(page, size).map { originalPage ->
            // Filter the page content by permissions
            val filteredContent = originalPage.content.filter { entity -> hasReadPermission(entity) }
            
            // Create a new page with filtered content
            // Note: This approach means pagination might not be accurate after filtering
            Page(
                content = filteredContent,
                pageNumber = originalPage.pageNumber,
                pageSize = originalPage.pageSize,
                totalElements = originalPage.totalElements, // This value is now approximate
                totalPages = originalPage.totalPages,      // This value is now approximate
                isFirst = originalPage.isFirst,
                isLast = originalPage.isLast
            )
        }
    }
    
    override fun search(query: String, page: Int, size: Int): Flow<Page<T>> {
        return delegate.search(query, page, size).map { originalPage ->
            // Filter the page content by permissions
            val filteredContent = originalPage.content.filter { entity -> hasReadPermission(entity) }
            
            // Create a new page with filtered content
            Page(
                content = filteredContent,
                pageNumber = originalPage.pageNumber,
                pageSize = originalPage.pageSize,
                totalElements = originalPage.totalElements, // Approximate
                totalPages = originalPage.totalPages,      // Approximate
                isFirst = originalPage.isFirst,
                isLast = originalPage.isLast
            )
        }
    }
    
    private fun hasReadPermission(entity: T): Boolean {
        val objectName = entity.getPermissionObjectName()
        val objectId = if (entity.requiresSpecificPermission()) entity.getPermissionObjectId() else null
        return permissionController.hasPermission(objectName, "read", objectId)
    }
}