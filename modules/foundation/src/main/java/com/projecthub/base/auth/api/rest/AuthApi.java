package com.projecthub.base.auth.api.rest;

import com.projecthub.base.auth.api.dto.AuthenticationResponse;
import com.projecthub.base.auth.api.dto.LoginRequestDTO;
import com.projecthub.base.auth.api.dto.RegisterRequestDTO;
import com.projecthub.base.user.api.dto.AppUserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Authentication", description = "Authentication API")
@RequestMapping("/api/v1/auth")
public interface AuthApi {

    @Operation(summary = "Register a new user",
        responses = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
        })
    @PostMapping("/register")
    ResponseEntity<AppUserDTO> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequest);

    @Operation(summary = "Authenticate user",
        responses = {
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
        })
    @PostMapping("/login")
    ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequestDTO loginRequest);
}
