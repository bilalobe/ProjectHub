package com.projecthub.base.auth.domain.exception;

import com.projecthub.base.shared.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AccountDisabledException extends BaseException {
    public AccountDisabledException(final String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public AccountDisabledException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.FORBIDDEN);
    }
}
