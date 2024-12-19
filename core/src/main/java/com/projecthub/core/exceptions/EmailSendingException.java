package com.projecthub.core.exceptions;

/**
 * Custom exception for email sending failures.
 */
public class EmailSendingException extends RuntimeException {
    public EmailSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}