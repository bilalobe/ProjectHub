package com.projecthub.base.auth.domain.exception;

import com.projecthub.base.shared.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends BaseException {
    public InvalidTokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
