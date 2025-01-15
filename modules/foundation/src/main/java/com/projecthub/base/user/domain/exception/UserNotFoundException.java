package com.projecthub.base.user.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * Exception thrown when a requested user is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(final UUID userId) {
        super("User not found with ID: " + userId.toString());
    }

    public UserNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
