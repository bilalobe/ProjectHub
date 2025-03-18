package com.projecthub.application.cqrs

/**
 * Interface for query handlers in CQRS pattern.
 * Query handlers execute queries and produce results.
 */
interface QueryHandler<Q : Query<R>, R> {
    /**
     * Execute the query and return a result.
     */
    suspend fun handle(query: Q): R
}
