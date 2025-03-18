package com.projecthub.auth.exception;

import com.projecthub.auth.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Exception thrown when password validation fails.
 */
public class PasswordValidationException extends BaseException {
    private final List<String> validationErrors;
    
    public PasswordValidationException(final String message, final List<String> validationErrors) {
        super(message, HttpStatus.BAD_REQUEST);
        this.validationErrors = validationErrors;
    }
    
    public PasswordValidationException(final String message, final List<String> validationErrors, final Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST);
        this.validationErrors = validationErrors;
    }
    
    /**
     * Get detailed validation error messages.
     * 
     * @return List of validation error messages
     */
    public List<String> getValidationErrors() {
        return validationErrors;
    }
}