package com.projecthub.core.controllers;

import com.projecthub.core.auth.service.AuthenticationService;
import com.projecthub.core.dto.AuthenticationResult;
import com.projecthub.core.services.auth.PasskeyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/passkey")
public class PasskeyController {
    private final PasskeyService passkeyService;
    private final AuthenticationService authService;

    public PasskeyController(PasskeyService passkeyService, AuthenticationService authService) {
        this.passkeyService = passkeyService;
        this.authService = authService;
    }

    @PostMapping("/register/start")
    public ResponseEntity<RegistrationRequest> startRegistration(Authentication auth) {
        return ResponseEntity.ok(passkeyService.startRegistration(
            ((AppUser) auth.getPrincipal())
        ));
    }

    @PostMapping("/register/finish")
    public ResponseEntity<Void> finishRegistration(
            Authentication auth,
            @RequestBody RegistrationResponse response) {
        passkeyService.finishRegistration((AppUser) auth.getPrincipal(), response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/authenticate/start")
    public ResponseEntity<AuthenticationRequest> startAuthentication(
            @RequestParam String username) {
        return ResponseEntity.ok(passkeyService.startAuthentication(username));
    }

    @PostMapping("/authenticate/finish")
    public ResponseEntity<AuthenticationResult> finishAuthentication(
            @RequestBody AuthenticationResponse response) {
        if (passkeyService.verifyAuthentication(response)) {
            return ResponseEntity.ok(authService.createAuthenticationResult(
                passkeyService.getUserFromCredentialId(response.getId())
            ));
        }
        return ResponseEntity.badRequest().build();
    }
}
