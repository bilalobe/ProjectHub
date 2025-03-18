package com.projecthub.base.security.exception;

/**
 * Exception thrown when a user attempts to access a resource without proper authorization.
 * This is a domain-specific security exception for the foundation module.
 */
public class AccessDeniedException extends RuntimeException {

    /**
     * Constructs an AccessDeniedException with the specified detail message.
     *
     * @param message The detail message
     */
    public AccessDeniedException(String message) {
        super(message);
    }

    /**
     * Constructs an AccessDeniedException with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The cause of the exception
     */
    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}