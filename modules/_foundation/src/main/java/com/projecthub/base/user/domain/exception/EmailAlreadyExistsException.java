package com.projecthub.base.user.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when attempting to use an email that already exists.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(final String message) {
        super(message);
    }

    public EmailAlreadyExistsException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
