package com.projecthub.base.auth.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when invalid credentials are provided.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(final String message) {
        super(message);
    }

    public InvalidCredentialsException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
