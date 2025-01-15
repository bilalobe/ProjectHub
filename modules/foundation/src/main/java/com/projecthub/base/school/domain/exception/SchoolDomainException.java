package com.projecthub.base.school.domain.exception;

public class SchoolDomainException extends RuntimeException {

    public SchoolDomainException(final String message) {
        super(message);
    }

    public SchoolDomainException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
