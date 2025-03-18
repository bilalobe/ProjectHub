package com.projecthub.domain.exceptions

/**
 * Base exception for all domain-related exceptions.
 * Used to indicate business rule violations.
 */
abstract class DomainException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) {
    abstract val code: String
}
