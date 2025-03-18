package com.projecthub.base.shared.exception;

import org.springframework.http.HttpStatus;

public class StorageException extends BaseException {
    public StorageException(final String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public StorageException(final String message, final Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
