package com.projecthub.base.user.domain.exception;

import com.projecthub.base.shared.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RegistrationFailedException extends BaseException {
    public RegistrationFailedException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public RegistrationFailedException(String message, Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
