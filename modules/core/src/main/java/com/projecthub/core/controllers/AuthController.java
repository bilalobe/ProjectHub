package com.projecthub.core.controllers;

import com.projecthub.core.api.AuthApi;
import com.projecthub.core.auth.service.AuthenticationService;
import com.projecthub.core.dto.*;
import com.projecthub.core.exceptions.AuthenticationFailedException;
import com.projecthub.core.exceptions.InvalidCredentialsException;
import com.projecthub.core.exceptions.UserAlreadyExistsException;
import com.projecthub.core.services.registration.AppUserRegistrationService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;


/**
 * AuthController is a REST controller that handles authentication-related requests.
 * It provides endpoints for user registration and login.
 */
@RestController
@RequestMapping("/api/v1/auth")  // Updated path
public class AuthController implements AuthApi {  // Added 'implements AuthApi'
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AppUserRegistrationService userRegistrationService;
    private final AuthenticationService authenticationService;

    public AuthController(
            AppUserRegistrationService userRegistrationService,
            AuthenticationService authenticationService) {
        this.userRegistrationService = userRegistrationService;
        this.authenticationService = authenticationService;
    }

    @Override
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

    @Override
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
}