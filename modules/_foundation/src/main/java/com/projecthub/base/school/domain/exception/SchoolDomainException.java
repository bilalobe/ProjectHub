package com.projecthub.base.school.domain.exception;

import com.projecthub.base.shared.exception.BaseException;
import org.springframework.http.HttpStatus;

public class SchoolDomainException extends BaseException {
    public SchoolDomainException(final String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public SchoolDomainException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST);
    }
}
