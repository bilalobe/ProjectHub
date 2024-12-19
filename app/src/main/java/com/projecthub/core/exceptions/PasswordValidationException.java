package com.projecthub.core.exceptions;

public class PasswordValidationException extends RuntimeException {
    public PasswordValidationException(String message) {
        super(message);
    }

    public PasswordValidationException(String string, PasswordValidationException e) {
        super(string, e);
    }
}
