package com.projecthub.base.auth.api.controller;

import com.projecthub.base.auth.api.dto.AuthenticationResponse;
import com.projecthub.base.auth.api.dto.RegistrationResponse;
import com.projecthub.base.auth.service.PasswordService;
import com.projecthub.base.auth.service.security.AuthenticationService;
import com.projecthub.base.user.domain.entity.AppUser;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/passkey")
@Tag(name = "Passkey", description = "Passkey authentication API")
@Validated
@Slf4j
@RequiredArgsConstructor
public class PasskeyController {
    private final PasswordService passkeyService;
    private final AuthenticationService authService;

    @Operation(summary = "Start passkey registration")
    @ApiResponse(responseCode = "200", description = "Registration options created")
    @PostMapping("/register/start")
    public ResponseEntity<PublicKeyCredentialCreationOptions> startRegistration(Authentication auth) {
        log.debug("Starting passkey registration for user: {}", auth.getName());
        return ResponseEntity.ok(passkeyService.startRegistration((AppUser) auth.getPrincipal()));
    }

    @Operation(summary = "Complete passkey registration")
    @ApiResponse(responseCode = "200", description = "Registration successful")
    @PostMapping("/register/finish")
    public ResponseEntity<Void> finishRegistration(
        Authentication auth,
        @Valid @RequestBody RegistrationResponse response) {
        log.debug("Finishing passkey registration for user: {}", auth.getName());
        passkeyService.finishRegistration((AppUser) auth.getPrincipal(), response);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Start passkey authentication")
    @ApiResponse(responseCode = "200", description = "Authentication options created")
    @PostMapping("/authenticate/start")
    public ResponseEntity<PublicKeyCredentialRequestOptions> startAuthentication(
        @RequestParam String username) {
        log.debug("Starting passkey authentication for user: {}", username);
        return ResponseEntity.ok(passkeyService.startAuthentication(username));
    }

    @Operation(summary = "Complete passkey authentication")
    @ApiResponse(responseCode = "200", description = "Authentication successful")
    @ApiResponse(responseCode = "400", description = "Authentication failed")
    @PostMapping("/authenticate/finish")
    public ResponseEntity<Object> finishAuthentication(
        @Valid @RequestBody AuthenticationResponse response) {
        log.debug("Finishing passkey authentication for credential: {}", response.getCredentialId());

        if (passkeyService.verifyAuthentication(response)) {
            var user = passkeyService.getUserFromCredentialId(response.getCredentialId());
            var result = authService.createAuthenticationResult(user);
            log.info("Passkey authentication successful for user: {}", user.getId());
            return ResponseEntity.ok(result);
        }

        log.warn("Passkey authentication failed for credential: {}", response.getCredentialId());
        return ResponseEntity.badRequest().build();
    }
}
