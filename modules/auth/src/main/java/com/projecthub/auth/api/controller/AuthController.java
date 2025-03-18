package com.projecthub.auth.api.controller;

import com.projecthub.auth.api.dto.LoginRequestDTO;
import com.projecthub.auth.api.dto.RegisterRequestDTO;
import com.projecthub.auth.api.rest.AuthApi;
import com.projecthub.auth.dto.AuthenticationResponse;
import com.projecthub.auth.exception.AuthenticationException;
import com.projecthub.auth.service.AuthenticationService;
import com.projecthub.auth.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller implementation for authentication-related endpoints.
 */
@RestController
public class AuthController implements AuthApi {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationService authenticationService;
    private final RegistrationService registrationService;

    public AuthController(
            AuthenticationService authenticationService,
            RegistrationService registrationService) {
        this.authenticationService = authenticationService;
        this.registrationService = registrationService;
    }

    @Override
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        logger.debug("Login attempt for user: {}", loginRequest.principal());
        
        try {
            // Get client IP address from request for audit logging
            AuthenticationResponse response = authenticationService.authenticate(loginRequest);
            logger.info("User authenticated successfully: {}", loginRequest.principal());
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for user: {}", loginRequest.principal());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Override
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        logger.debug("Registration attempt for user: {}", registerRequest.username());
        
        if (registrationService.isUsernameTaken(registerRequest.username())) {
            logger.info("Registration failed: username already taken - {}", registerRequest.username());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        if (registrationService.isEmailTaken(registerRequest.email())) {
            logger.info("Registration failed: email already taken - {}", registerRequest.email());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        registrationService.registerUser(registerRequest);
        logger.info("User registered successfully: {}", registerRequest.username());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> validateToken(@RequestBody String token) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
        boolean isValid = authenticationService.validateToken(token);
        if (isValid) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}