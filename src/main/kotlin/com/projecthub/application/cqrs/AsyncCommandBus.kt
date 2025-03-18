package com.projecthub.application.cqrs

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

/**
 * Enhanced implementation of a command bus that supports coroutines for asynchronous command execution.
 */
@Component
@Primary
class AsyncCommandBus(
    handlers: List<CommandHandler<*, *>>,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val logger = LoggerFactory.getLogger(AsyncCommandBus::class.java)
    private val handlerMap = mutableMapOf<KClass<out Command<*>>, CommandHandler<Command<Any>, Any>>()

    init {
        @Suppress("UNCHECKED_CAST")
        handlers.forEach { handler ->
            val commandType = findCommandType(handler::class.java)
            if (commandType != null) {
                handlerMap[commandType.kotlin as KClass<out Command<*>>] = handler as CommandHandler<Command<Any>, Any>
            }
        }
        logger.info("AsyncCommandBus initialized with ${handlerMap.size} handlers")
    }

    /**
     * Dispatch a command asynchronously to its appropriate handler and return the result.
     * Execution happens on the specified coroutine dispatcher.
     */
    suspend fun <R> dispatch(command: Command<R>): R = withContext(dispatcher) {
        val handler = handlerMap[command::class]
            ?: throw NoHandlerFoundException("No handler found for command: ${command.commandName}")

        logger.debug("Dispatching command: ${command.commandName}")

        try {
            @Suppress("UNCHECKED_CAST")
            handler.handle(command) as R
        } catch (e: Exception) {
            logger.error("Error handling command ${command.commandName}", e)
            throw CommandExecutionException("Failed to execute command ${command.commandName}", e)
        }
    }

    private fun findCommandType(handlerClass: Class<*>): Class<*>? {
        // ... existing code ...
        val interfaces = handlerClass.genericInterfaces
        return interfaces.find {
            it.toString().contains("CommandHandler")
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

class CommandExecutionException(message: String, cause: Throwable) : RuntimeException(message, cause)
