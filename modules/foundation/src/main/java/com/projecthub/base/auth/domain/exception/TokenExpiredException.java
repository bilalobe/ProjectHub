package com.projecthub.base.auth.domain.exception;

import com.projecthub.base.shared.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TokenExpiredException extends BaseException {
    public TokenExpiredException(final String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public TokenExpiredException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.UNAUTHORIZED);
    }
}
