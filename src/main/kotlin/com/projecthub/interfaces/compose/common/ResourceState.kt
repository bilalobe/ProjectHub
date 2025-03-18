package com.projecthub.interfaces.ui.common

/**
 * A wrapper class that represents the current state of a resource (data) operation.
 * This provides consistent error and loading state handling across the application.
 */
sealed class ResourceState<out T> {
    /**
     * The operation is in progress.
     */
    object Loading : ResourceState<Nothing>()
    
    /**
     * The operation completed successfully with data.
     */
    data class Success<T>(val data: T) : ResourceState<T>()
    
    /**
     * The operation failed with an error.
     */
    data class Error(val exception: Throwable, val message: String = exception.localizedMessage ?: "Unknown error") : ResourceState<Nothing>()
    
    /**
     * Check if the current state is [Loading].
     */
    val isLoading: Boolean get() = this is Loading
    
    /**
     * Check if the current state is [Success].
     */
    val isSuccess: Boolean get() = this is Success
    
    /**
     * Check if the current state is [Error].
     */
    val isError: Boolean get() = this is Error
    
    /**
     * Get the data if state is [Success], otherwise return null.
     */
    fun getDataOrNull(): T? = if (this is Success) data else null
}