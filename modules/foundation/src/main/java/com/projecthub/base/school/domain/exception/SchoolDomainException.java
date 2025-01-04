package com.projecthub.base.school.domain.exception;

public class SchoolDomainException extends RuntimeException {

    public SchoolDomainException(String message) {
        super(message);
    }

    public SchoolDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
