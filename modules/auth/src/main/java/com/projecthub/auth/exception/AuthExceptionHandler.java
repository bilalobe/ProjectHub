package com.projecthub.auth.exception;

import com.projecthub.auth.dto.AuthErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AuthExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<AuthErrorResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        AuthErrorResponse errorResponse = new AuthErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Authentication Failed",
            ex.getMessage(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<AuthErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        AuthErrorResponse errorResponse = new AuthErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "Access Denied",
            ex.getMessage(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(com.projecthub.auth.exception.AuthorizationException.class)
    public ResponseEntity<AuthErrorResponse> handleAuthorizationException(
            com.projecthub.auth.exception.AuthorizationException ex, WebRequest request) {
        String message = ex.getRequiredPermission() != null ?
            String.format("Missing required permission: %s", ex.getRequiredPermission()) :
            ex.getMessage();
        
        AuthErrorResponse errorResponse = new AuthErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "Authorization Failed",
            message,
            request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AuthErrorResponse> handleAllOtherExceptions(
            Exception ex, WebRequest request) {
        AuthErrorResponse errorResponse = new AuthErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred",
            request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}