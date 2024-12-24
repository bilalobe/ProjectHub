package com.projecthub.core.controllers.advice;

import com.projecthub.core.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private ResponseEntity<ApiError> createErrorResponse(HttpStatus status, String message, Exception ex) {
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .message(message)
                .debugMessage(ex.getLocalizedMessage())
                .build();

        LOG.error("Error occurred: {}", message, ex);
        return new ResponseEntity<>(apiError, status);
    }

    // Base exception handler
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiError> handleBaseException(BaseException ex, Locale locale) {
        return createErrorResponse(ex.getStatus(), getLocalizedMessage(ex.getMessage(), locale), ex);
    }

    // Authentication and Authorization exceptions
    @ExceptionHandler({
            UnauthorizedAccessException.class,
            InvalidCredentialsException.class,
            AccessDeniedException.class,
            AccountDisabledException.class,
            TokenExpiredException.class
    })
    public ResponseEntity<ApiError> handleAuthException(Exception ex, Locale locale) {
        HttpStatus status;
        if (ex instanceof AccessDeniedException || ex instanceof AccountDisabledException) {
            status = HttpStatus.FORBIDDEN;
        } else if (ex instanceof UnauthorizedAccessException ||
                ex instanceof InvalidCredentialsException ||
                ex instanceof TokenExpiredException) {
            status = HttpStatus.UNAUTHORIZED;
        } else {
            status = HttpStatus.UNAUTHORIZED;
        }

        return createErrorResponse(
                status,
                getLocalizedMessage("error.authentication", locale),
                ex
        );
    }

    @ExceptionHandler({
            AuthenticationException.class,
            AuthenticationFailedException.class,
            InvalidTokenException.class
    })
    public ResponseEntity<ApiError> handleAuthenticationFailure(BaseException ex, Locale locale) {
        return createErrorResponse(HttpStatus.UNAUTHORIZED,
                getLocalizedMessage("error.authentication.failed", locale), ex);
    }

    @ExceptionHandler({
            EmailSendingException.class
    })
    public ResponseEntity<ApiError> handleEmailError(EmailSendingException ex, Locale locale) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                getLocalizedMessage("error.email.sending", locale), ex);
    }

    @ExceptionHandler({
            InvalidInputException.class,
            PasswordValidationException.class
    })
    public ResponseEntity<ApiError> handleInputValidation(BaseException ex, Locale locale) {
        return createErrorResponse(HttpStatus.BAD_REQUEST,
                getLocalizedMessage("error.input.validation", locale), ex);
    }

    @ExceptionHandler(RegistrationFailedException.class)
    public ResponseEntity<ApiError> handleRegistrationFailure(RegistrationFailedException ex, Locale locale) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                getLocalizedMessage("error.registration", locale), ex);
    }

    // Resource conflict exceptions
    @ExceptionHandler({
            UserAlreadyExistsException.class,
            EmailAlreadyExistsException.class,
            UsernameAlreadyExistsException.class,
            ConflictException.class,
            UserCreationException.class,
            UserUpdateException.class
    })
    public ResponseEntity<ApiError> handleConflictException(RuntimeException ex, Locale locale) {
        return createErrorResponse(
                HttpStatus.CONFLICT,
                getLocalizedMessage("error.conflict", locale),
                ex
        );
    }

    // Validation exceptions
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        ApiError error = handleValidationErrors(ex, LocaleContextHolder.getLocale(), "error.validation").getBody();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiError> handleBindException(BindException ex, Locale locale) {
        return handleValidationErrors(ex, locale, "error.binding");
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(ValidationException ex, Locale locale) {
        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                getLocalizedMessage("error.validation", locale),
                ex
        );
    }

    private ResponseEntity<ApiError> handleValidationErrors(Exception ex, Locale locale, String messageKey) {
        List<ApiSubError> subErrors = new ArrayList<>();
        BindingResult bindingResult = switch (ex) {
            case MethodArgumentNotValidException methodEx -> methodEx.getBindingResult();
            case BindException bindEx -> bindEx.getBindingResult();
            default -> null;
        };

        if (bindingResult != null) {
            bindingResult.getFieldErrors().forEach(error ->
                    subErrors.add(new ApiValidationError(
                            error.getField(),
                            error.getRejectedValue(),
                            messageSource.getMessage(error, locale)
                    ))
            );
        }

        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST)
                .message(getLocalizedMessage(messageKey, locale))
                .subErrors(subErrors)
                .build();

        LOG.error("Validation error: {}", messageKey, ex);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    // Resource not found exceptions
    @ExceptionHandler({
            ResourceNotFoundException.class,
            UserNotFoundException.class
    })
    public ResponseEntity<ApiError> handleNotFoundException(RuntimeException ex, Locale locale) {
        return createErrorResponse(
                HttpStatus.NOT_FOUND,
                getLocalizedMessage("error.not.found", locale),
                ex
        );
    }

    // Internal server errors
    @ExceptionHandler({
            RepositoryException.class,
            IOException.class,
            InternalServerException.class,
            SynchronizationException.class
    })
    public ResponseEntity<ApiError> handleInternalError(Exception ex, Locale locale) {
        return createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                getLocalizedMessage("error.internal", locale),
                ex
        );
    }

    @ExceptionHandler(SynchronizationException.class)
    public ResponseEntity<ApiError> handleSynchronizationException(SynchronizationException ex, Locale locale) {
        return createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                getLocalizedMessage("error.synchronization", locale),
                ex
        );
    }

    // Profile update exception
    @ExceptionHandler(ProfileUpdateException.class)
    public ResponseEntity<ApiError> handleProfileUpdateException(ProfileUpdateException ex) {
        LOG.error("Profile update error", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .message("Failed to update profile")
                        .debugMessage(ex.getMessage())
                        .build());
    }

    // Storage exception
    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ApiError> handleStorageException(StorageException ex) {
        LOG.error("File storage error", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .message("File storage error")
                        .debugMessage(ex.getMessage())
                        .build());
    }

    // Catch-all handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex, Locale locale) {
        return createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                getLocalizedMessage("error.unexpected", locale),
                ex
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllOtherExceptions(
            Exception ex, WebRequest request) {
        LOG.error("Unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                           .body(ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .message("An unexpected error occurred")
                                .debugMessage(ex.getLocalizedMessage())
                                .build());
    }

    private String getLocalizedMessage(String key, Locale locale) {
        return messageSource.getMessage(key, null, key, locale);
    }
}