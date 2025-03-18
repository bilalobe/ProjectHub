package com.projecthub.base.auth.api.controller;

import com.projecthub.base.auth.api.dto.AuthenticationResponse;
import com.projecthub.base.auth.api.dto.LoginRequestDTO;
import com.projecthub.base.auth.api.dto.RegisterRequestDTO;
import com.projecthub.base.auth.api.rest.AuthApi;
import com.projecthub.base.auth.application.registration.AppUserRegistrationService;
import com.projecthub.base.auth.domain.event.AuthEventPublisher;
import com.projecthub.base.auth.service.fortress.FortressAuthenticationManager;
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
    private final FortressAuthenticationManager fortressAuthManager;
    private final AuthEventPublisher eventPublisher;
    private final AuthenticationService legacyAuthService; // Keep for backwards compatibility

    @Operation(summary = "Register new user")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "409", description = "User already exists")
    @PostMapping("/register")
    public ResponseEntity<AppUserDTO> registerUser(
        @Valid @RequestBody final RegisterRequestDTO request) {
        AuthController.log.debug("Processing registration request for user: {}", request.username());

        final var user = this.userRegistrationService.registerUser(request);
        this.eventPublisher.publishUserRegistered(user.id());

        AuthController.log.info("User registered successfully: {}", user.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Override
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody final LoginRequestDTO loginRequest) {
        AuthController.log.debug("Processing login request for user: {}", loginRequest.principal());

        // Use Fortress authentication manager
        final var authResult = this.fortressAuthManager.authenticate(loginRequest);
        this.eventPublisher.publishUserLoggedIn(authResult.userId());

        // Use legacy service to get user details
        final var user = AuthenticationService.getCurrentUser();
        final var response = new AuthenticationResponse(authResult.token(), user);
        return ResponseEntity.ok(response);
    }
}
