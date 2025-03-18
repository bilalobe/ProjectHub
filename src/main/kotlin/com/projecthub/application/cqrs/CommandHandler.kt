package com.projecthub.application.cqrs

/**
 * Interface for command handlers in CQRS pattern.
 * Command handlers execute commands and produce results.
 */
interface CommandHandler<C : Command<R>, R> {
    /**
     * Execute the command and return a result.
     */
    suspend fun handle(command: C): R
}
