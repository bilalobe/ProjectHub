package com.projecthub.core.exceptions;

import org.springframework.http.HttpStatus;

public class EmailSendingException extends BaseException {
    public EmailSendingException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public EmailSendingException(String message, Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}