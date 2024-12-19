package com.projecthub.core.controllers;

import com.projecthub.core.dto.AppUserDTO;
import com.projecthub.core.dto.LoginRequestDTO;
import com.projecthub.core.dto.RegisterRequestDTO;
import com.projecthub.core.dto.AuthResponseDTO;
import com.projecthub.core.dto.AuthenticationResult;
import com.projecthub.core.dto.ErrorResponse;
import com.projecthub.core.exceptions.UserAlreadyExistsException;
import com.projecthub.core.exceptions.InvalidCredentialsException;
import com.projecthub.core.exceptions.AuthenticationFailedException;
import com.projecthub.core.services.user.UserRegistrationService;
import com.projecthub.core.services.auth.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * AuthController is a REST controller that handles authentication-related requests.
 * It provides endpoints for user registration and login.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    // Removed unused fields
    private final UserRegistrationService userRegistrationService;
    private final AuthenticationService authenticationService;

    public AuthController(
            UserRegistrationService userRegistrationService,
            AuthenticationService authenticationService) {
        this.userRegistrationService = userRegistrationService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<AppUserDTO> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        try {
            AppUserDTO user = userRegistrationService.registerUser(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (UserAlreadyExistsException e) {
            logger.warn("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            logger.error("Unexpected error during registration for user: {}", registerRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            AuthenticationResult result = authenticationService.authenticate(loginRequest);
            // Modified to use existing constructor
            AuthResponseDTO authResponse = new AuthResponseDTO(result.getToken());
            return ResponseEntity.ok(authResponse);
        } catch (BadCredentialsException e) {
            logger.warn("Login failed for user {}: Invalid credentials", loginRequest.getUsername());
            throw new InvalidCredentialsException("Invalid username or password");
        } catch (Exception e) {
            logger.error("Login failed for user {}", loginRequest.getUsername(), e);
            throw new AuthenticationFailedException("Authentication failed");
        }
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "User registration failed",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Authentication failed",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors
        );
        return ResponseEntity.badRequest().body(error);
    }
}