package com.projecthub.application.exceptions

/**
 * Base exception for all application-level exceptions.
 * Used to indicate errors in application flow.
 */
abstract class ApplicationException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) {
    abstract val code: String
}
