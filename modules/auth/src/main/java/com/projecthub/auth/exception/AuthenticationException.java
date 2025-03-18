package com.projecthub.auth.exception;

import com.projecthub.auth.common.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication fails.
 */
public class AuthenticationException extends BaseException {
    
    public AuthenticationException(final String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public AuthenticationException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.UNAUTHORIZED);
    }
}