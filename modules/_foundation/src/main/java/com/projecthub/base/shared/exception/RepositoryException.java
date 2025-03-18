package com.projecthub.base.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception for repository operations.
 */
public class RepositoryException extends BaseException {
    public RepositoryException(final String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public RepositoryException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
