package com.projecthub.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception for repository operations.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class RepositoryException extends RuntimeException {
    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
