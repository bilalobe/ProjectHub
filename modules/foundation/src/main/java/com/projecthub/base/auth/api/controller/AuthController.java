package com.projecthub.base.auth.api.controller;

import com.projecthub.base.auth.api.dto.AuthenticationResponse;
import com.projecthub.base.auth.api.dto.LoginRequestDTO;
import com.projecthub.base.auth.api.dto.RegisterRequestDTO;
import com.projecthub.base.auth.api.rest.AuthApi;
import com.projecthub.base.auth.application.registration.AppUserRegistrationService;
import com.projecthub.base.auth.domain.event.AuthEventPublisher;
import com.projecthub.base.auth.service.security.AuthenticationService;
import com.projecthub.base.user.api.dto.AppUserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication management API")
@Validated
@Slf4j
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AppUserRegistrationService userRegistrationService;
    private final AuthenticationService authenticationService;
    private final AuthEventPublisher eventPublisher;

    @Operation(summary = "Register new user")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "409", description = "User already exists")
    @PostMapping("/register")
    public ResponseEntity<AppUserDTO> registerUser(
        @Valid @RequestBody RegisterRequestDTO request) {
        log.debug("Processing registration request for user: {}", request.username());

        var user = userRegistrationService.registerUser(request);
        eventPublisher.publishUserRegistered(user.id());

        log.info("User registered successfully: {}", user.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Override
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        log.debug("Processing login request for user: {}", loginRequest.principal());

        var authResult = authenticationService.authenticate(loginRequest);
        eventPublisher.publishUserLoggedIn(authResult.userId());

        var user = authenticationService.getCurrentUser();
        var response = new AuthenticationResponse(authResult.token(), user);
        return ResponseEntity.ok(response);
    }
}
