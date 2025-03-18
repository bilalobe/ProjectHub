package com.projecthub.application.cqrs

/**
 * Marker interface for queries in CQRS pattern.
 * Queries represent a request to retrieve data without changing state.
 */
interface Query<R> {
    /**
     * Unique identifier for the query type.
     * Used for routing and logging.
     */
    val queryName: String get() = this::class.simpleName ?: "UnknownQuery"
}
