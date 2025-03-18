package com.projecthub.application.cqrs

/**
 * Marker interface for commands in CQRS pattern.
 * Commands represent an intention to change the state of the system.
 */
interface Command<R> {
    /**
     * Unique identifier for the command type.
     * Used for routing and logging.
     */
    val commandName: String get() = this::class.simpleName ?: "UnknownCommand"
}
