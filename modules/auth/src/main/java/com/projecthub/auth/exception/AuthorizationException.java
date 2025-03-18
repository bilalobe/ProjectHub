package com.projecthub.auth.exception;

import com.projecthub.auth.common.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a user attempts to access a resource without proper authorization.
 */
public class AuthorizationException extends BaseException {
    
    public AuthorizationException(final String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public AuthorizationException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.FORBIDDEN);
    }
}