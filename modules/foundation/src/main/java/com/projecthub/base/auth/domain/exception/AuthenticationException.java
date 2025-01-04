package com.projecthub.base.auth.domain.exception;

import com.projecthub.base.shared.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AuthenticationException extends BaseException {
    public AuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.UNAUTHORIZED);
    }
}
