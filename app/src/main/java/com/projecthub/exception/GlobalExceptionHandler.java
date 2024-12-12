package com.projecthub.exception;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global exception handler for the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex, Locale locale) {
        String message = messageSource.getMessage("error.resource.not.found", null, locale);
        ApiError apiError = ApiError.of(HttpStatus.NOT_FOUND, message, ex.getMessage());
        logger.error("ResourceNotFoundException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex, Locale locale) {
        String message = messageSource.getMessage("error.illegal.argument", null, locale);
        ApiError apiError = ApiError.of(HttpStatus.BAD_REQUEST, message, ex.getMessage());
        logger.error("IllegalArgumentException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiError> handleIOException(IOException ex, Locale locale) {
        String message = messageSource.getMessage("error.io.exception", null, locale);
        ApiError apiError = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR, message, ex.getMessage());
        logger.error("IOException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex, Locale locale) {
        String message = messageSource.getMessage("error.access.denied", null, locale);
        ApiError apiError = ApiError.of(HttpStatus.FORBIDDEN, message, ex.getMessage());
        logger.error("AccessDeniedException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex, Locale locale) {
        List<ApiSubError> subErrors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String msg = messageSource.getMessage(error, locale);
            ApiValidationError validationError = new ApiValidationError(
                    error.getField(),
                    error.getRejectedValue(),
                    msg
            );
            subErrors.add(validationError);
        });
        String detail = "Validation failed for fields.";
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST)
                .message(detail)
                .subErrors(subErrors)
                .build();
        logger.error("MethodArgumentNotValidException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiError> handleBindException(BindException ex, Locale locale) {
        List<ApiSubError> subErrors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String msg = messageSource.getMessage(error, locale);
            ApiValidationError validationError = new ApiValidationError(
                    error.getField(),
                    error.getRejectedValue(),
                    msg
            );
            subErrors.add(validationError);
        });
        String detail = "Binding failed for fields.";
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST)
                .message(detail)
                .subErrors(subErrors)
                .build();
        logger.error("BindException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentialsException(InvalidCredentialsException ex, Locale locale) {
        String message = messageSource.getMessage("error.invalid.credentials", null, locale);
        ApiError apiError = ApiError.of(HttpStatus.UNAUTHORIZED, message, ex.getMessage());
        logger.error("InvalidCredentialsException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExistsException(UserAlreadyExistsException ex, Locale locale) {
        String message = messageSource.getMessage("error.user.already.exists", null, locale);
        ApiError apiError = ApiError.of(HttpStatus.CONFLICT, message, ex.getMessage());
        logger.error("UserAlreadyExistsException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiError> handleUnauthorizedAccessException(UnauthorizedAccessException ex, Locale locale) {
        String message = messageSource.getMessage("error.unauthorized.access", null, locale);
        ApiError apiError = ApiError.of(HttpStatus.UNAUTHORIZED, message, ex.getMessage());
        logger.error("UnauthorizedAccessException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiError> handleInvalidInputException(InvalidInputException ex, Locale locale) {
        String message = messageSource.getMessage("error.invalid.input", null, locale);
        ApiError apiError = ApiError.of(HttpStatus.BAD_REQUEST, message, ex.getMessage());
        logger.error("InvalidInputException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflictException(ConflictException ex, Locale locale) {
        String message = messageSource.getMessage("error.conflict", null, locale);
        ApiError apiError = ApiError.of(HttpStatus.CONFLICT, message, ex.getMessage());
        logger.error("ConflictException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex, Locale locale) {
        String message = messageSource.getMessage("error.internal.server", null, locale);
        ApiError apiError = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR, message, ex.getMessage());
        logger.error("Exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}