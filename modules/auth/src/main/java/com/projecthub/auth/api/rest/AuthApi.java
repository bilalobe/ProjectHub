package com.projecthub.auth.api.rest;

import com.projecthub.auth.api.dto.LoginRequestDTO;
import com.projecthub.auth.api.dto.RegisterRequestDTO;
import com.projecthub.auth.dto.AuthenticationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * API for authentication operations.
 */
@Tag(name = "Authentication", description = "Authentication API")
@RequestMapping("/api/v1/auth")
public interface AuthApi {

    @Operation(summary = "Authenticate user",
        responses = {
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
        })
    @PostMapping("/login")
    ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequestDTO loginRequest);
    
    @Operation(summary = "Register a new user",
        responses = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
        })
    @PostMapping("/register")
    ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestDTO registerRequest);
    
    @Operation(summary = "Validate token",
        responses = {
            @ApiResponse(responseCode = "200", description = "Token is valid"),
            @ApiResponse(responseCode = "401", description = "Invalid token")
        })
    @PostMapping("/validate")
    ResponseEntity<Void> validateToken(@RequestBody String token);
}