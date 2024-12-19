package com.projecthub.core.exceptions;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested user is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID userId) {
        super("User not found with ID: " + userId.toString());
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}