package com.projecthub.application.cqrs

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

/**
 * Implementation of a query bus that routes queries to their handlers.
 */
@Component
class QueryBus(handlers: List<QueryHandler<*, *>>) {
    private val logger = LoggerFactory.getLogger(QueryBus::class.java)
    private val handlerMap = mutableMapOf<KClass<out Query<*>>, QueryHandler<Query<Any>, Any>>()

    init {
        @Suppress("UNCHECKED_CAST")
        handlers.forEach { handler ->
            // Use reflection to determine which query type this handler supports
            val queryType = findQueryType(handler::class.java)
            if (queryType != null) {
                handlerMap[queryType.kotlin as KClass<out Query<*>>] = handler as QueryHandler<Query<Any>, Any>
            }
        }
        logger.info("QueryBus initialized with ${handlerMap.size} handlers")
    }

    /**
     * Dispatch a query to its appropriate handler and return the result.
     */
    suspend fun <R> dispatch(query: Query<R>): R {
        val handler = handlerMap[query::class]
            ?: throw NoHandlerFoundException("No handler found for query: ${query.queryName}")

        logger.debug("Dispatching query: ${query.queryName}")

        @Suppress("UNCHECKED_CAST")
        return handler.handle(query) as R
    }

    private fun findQueryType(handlerClass: Class<*>): Class<*>? {
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

class NoHandlerFoundException(message: String) : RuntimeException(message)
