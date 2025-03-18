package com.projecthub.base.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user attempts to access a resource or perform an action
 * for which they do not have sufficient permissions.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs an UnauthorizedException with a default message.
     */
    public UnauthorizedException() {
        super("You do not have permission to perform this action");
    }

    /**
     * Constructs an UnauthorizedException with a custom message.
     *
     * @param message The detailed message explaining why the access was denied
     */
    public UnauthorizedException(String message) {
        super(message);
    }

    /**
     * Constructs an UnauthorizedException with a custom message and cause.
     *
     * @param message The detailed message explaining why the access was denied
     * @param cause The underlying cause of the unauthorized access
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an UnauthorizedException with a cause.
     *
     * @param cause The underlying cause of the unauthorized access
     */
    public UnauthorizedException(Throwable cause) {
        super("You do not have permission to perform this action", cause);
    }
}