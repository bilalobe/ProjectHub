package com.projecthub.interfaces.ui.data

import com.projecthub.interfaces.ui.common.ResourceState
import kotlinx.coroutines.flow.Flow

/**
 * Base repository interface for consistent data access patterns.
 * Ensures all repositories follow the same structure and use Flows for reactive data.
 * 
 * @param T The type of entity this repository manages
 * @param ID The type of ID used to identify entities
 */
interface Repository<T, ID> {
    /**
     * Get all entities as a Flow
     */
    fun getAll(): Flow<ResourceState<List<T>>>
    
    /**
     * Get an entity by its ID as a Flow
     */
    fun getById(id: ID): Flow<ResourceState<T>>
    
    /**
     * Create a new entity
     */
    suspend fun create(item: T): ResourceState<T>
    
    /**
     * Update an existing entity
     */
    suspend fun update(item: T): ResourceState<T>
    
    /**
     * Delete an entity by its ID
     */
    suspend fun delete(id: ID): ResourceState<Boolean>
    
    /**
     * Search for entities matching the query
     */
    fun search(query: String): Flow<ResourceState<List<T>>>
}