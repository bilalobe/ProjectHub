package com.projecthub.application.cqrs

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

/**
 * Implementation of a command bus that routes commands to their handlers.
 */
@Component
class CommandBus(handlers: List<CommandHandler<*, *>>) {
    private val logger = LoggerFactory.getLogger(CommandBus::class.java)
    private val handlerMap = mutableMapOf<KClass<out Command<*>>, CommandHandler<Command<Any>, Any>>()

    init {
        @Suppress("UNCHECKED_CAST")
        handlers.forEach { handler ->
            // Use reflection to determine which command type this handler supports
            val commandType = findCommandType(handler::class.java)
            if (commandType != null) {
                handlerMap[commandType.kotlin as KClass<out Command<*>>] = handler as CommandHandler<Command<Any>, Any>
            }
        }
        logger.info("CommandBus initialized with ${handlerMap.size} handlers")
    }

    /**
     * Dispatch a command to its appropriate handler and return the result.
     */
    suspend fun <R> dispatch(command: Command<R>): R {
        val handler = handlerMap[command::class]
            ?: throw NoHandlerFoundException("No handler found for command: ${command.commandName}")

        logger.debug("Dispatching command: ${command.commandName}")

        @Suppress("UNCHECKED_CAST")
        return handler.handle(command) as R
    }

    private fun findCommandType(handlerClass: Class<*>): Class<*>? {
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

class NoHandlerFoundException(message: String) : RuntimeException(message)
