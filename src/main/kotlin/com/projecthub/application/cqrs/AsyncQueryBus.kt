package com.projecthub.application.cqrs

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

/**
 * Enhanced implementation of a query bus that supports coroutines for asynchronous query execution.
 */
@Component
@Primary
class AsyncQueryBus(
    handlers: List<QueryHandler<*, *>>,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val logger = LoggerFactory.getLogger(AsyncQueryBus::class.java)
    private val handlerMap = mutableMapOf<KClass<out Query<*>>, QueryHandler<Query<Any>, Any>>()

    init {
        @Suppress("UNCHECKED_CAST")
        handlers.forEach { handler ->
            val queryType = findQueryType(handler::class.java)
            if (queryType != null) {
                handlerMap[queryType.kotlin as KClass<out Query<*>>] = handler as QueryHandler<Query<Any>, Any>
            }
        }
        logger.info("AsyncQueryBus initialized with ${handlerMap.size} handlers")
    }

    /**
     * Dispatch a query asynchronously to its appropriate handler and return the result.
     * Execution happens on the specified coroutine dispatcher.
     */
    suspend fun <R> dispatch(query: Query<R>): R = withContext(dispatcher) {
        val handler = handlerMap[query::class]
            ?: throw NoHandlerFoundException("No handler found for query: ${query.queryName}")

        logger.debug("Dispatching query: ${query.queryName}")

        try {
            @Suppress("UNCHECKED_CAST")
            handler.handle(query) as R
        } catch (e: Exception) {
            logger.error("Error handling query ${query.queryName}", e)
            throw QueryExecutionException("Failed to execute query ${query.queryName}", e)
        }
    }

    private fun findQueryType(handlerClass: Class<*>): Class<*>? {
        // ... existing code ...
        val interfaces = handlerClass.genericInterfaces
        return interfaces.find {
            it.toString().contains("QueryHandler")
        }?.let {
            val typeArg = it.toString().split("<")[1].split(",")[0]
            try {
                Class.forName(typeArg)
            } catch (e: ClassNotFoundException) {
                null
            }
        }
    }
}

class QueryExecutionException(message: String, cause: Throwable) : RuntimeException(message, cause)
