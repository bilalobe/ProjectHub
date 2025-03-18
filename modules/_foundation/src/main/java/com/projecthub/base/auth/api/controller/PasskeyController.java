package com.projecthub.base.auth.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.projecthub.base.auth.api.dto.*;
import com.projecthub.base.auth.domain.event.AuthEventPublisher;
import com.projecthub.base.auth.service.fortress.FortressPasskeyService;
import com.projecthub.base.auth.service.security.TokenManagementService;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import org.apache.directory.fortress.core.model.Session;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller for managing passkey (WebAuthn) authentication and registration.
 */
@RestController
@RequestMapping("/api/v1/auth/passkey")
public class PasskeyController {

    private static final Logger log = LoggerFactory.getLogger(PasskeyController.class);

    private final FortressPasskeyService passkeyService;
    private final TokenManagementService tokenService;
    private final AuthEventPublisher eventPublisher;

    public PasskeyController(
            FortressPasskeyService passkeyService,
            TokenManagementService tokenService,
            AuthEventPublisher eventPublisher) {
        this.passkeyService = passkeyService;
        this.tokenService = tokenService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Start the passkey registration process.
     * This endpoint requires the user to be authenticated.
     *
     * @return registration options to be used by the client
     */
    @PostMapping("/register/start")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PasskeyRegistrationResponseDTO> startRegistration() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            log.info("Starting passkey registration for user: {}", username);

            PublicKeyCredentialCreationOptions options = passkeyService.startPasskeyRegistration(username);

            PasskeyRegistrationResponseDTO response = new PasskeyRegistrationResponseDTO(
                options.toJson(),
                true,
                "Passkey registration started successfully"
            );

            return ResponseEntity.ok(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("Error starting passkey registration: {}", e.getMessage(), e);

            PasskeyRegistrationResponseDTO response = new PasskeyRegistrationResponseDTO(
                null,
                false,
                "Failed to start passkey registration: " + e.getMessage()
            );

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Complete the passkey registration process.
     * This endpoint requires the user to be authenticated.
     *
     * @param request the registration completion request
     * @return result of the registration process
     */
    @PostMapping("/register/finish")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> finishRegistration(
            @RequestBody PasskeyRegistrationRequestDTO request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            log.info("Finishing passkey registration for user: {}", username);

            boolean success = passkeyService.finishPasskeyRegistration(
                username,
                request.credentialResponse(),
                request.requestOptions()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", Boolean.valueOf(success));

            if (success) {
                response.put("message", "Passkey registered successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Failed to register passkey");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            log.error("Error completing passkey registration: {}", e.getMessage(), e);

            @NonNls Map<String, Object> response = new HashMap<>();
            response.put("success", Boolean.FALSE);
            response.put("message", "Error: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Start the passkey authentication process.
     * This endpoint is public and does not require authentication.
     *
     * @return authentication options to be used by the client
     */
    @PostMapping("/authenticate/start")
    public ResponseEntity<PasskeyAuthenticationResponseDTO> startAuthentication() {
        try {
            log.info("Starting passkey authentication flow");

            AssertionRequest request = passkeyService.startPasskeyAuthentication();

            PasskeyAuthenticationResponseDTO response = new PasskeyAuthenticationResponseDTO(
                request.toJson(),
                true,
                "Passkey authentication started"
            );

            return ResponseEntity.ok(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("Error starting passkey authentication: {}", e.getMessage(), e);

            PasskeyAuthenticationResponseDTO response = new PasskeyAuthenticationResponseDTO(
                null,
                false,
                "Failed to start passkey authentication: " + e.getMessage()
            );

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Complete the passkey authentication process.
     * This endpoint is public and does not require authentication.
     *
     * @param request the authentication completion request
     * @return authentication result with token if successful
     */
    @PostMapping("/authenticate/finish")
    public ResponseEntity<AuthenticationResultDTO> finishAuthentication(
            @RequestBody PasskeyAuthenticationRequestDTO request) {
        try {
            log.info("Finishing passkey authentication");

            Optional<Session> sessionOpt = passkeyService.finishPasskeyAuthentication(
                request.assertionResponse(),
                request.requestOptions()
            );

            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                String username = session.getUser().getName();

                // Convert Fortress userId (potentially a String) to UUID
                String fortressUserId = session.getUser().getUserId();
                UUID userId;

                try {
                    userId = UUID.fromString(fortressUserId);
                } catch (IllegalArgumentException e) {
                    // If not a valid UUID, generate a new one
                    userId = UUID.randomUUID();
                    log.warn("Invalid UUID format in Fortress user ID, generated new: {}", userId);
                }

                // Generate auth token
                String token = TokenManagementService.generateAccessToken(userId);

                // Publish login event
                eventPublisher.publishUserLoggedIn(userId);

                log.info("Passkey authentication successful for user: {}", username);

                return ResponseEntity.ok(new AuthenticationResultDTO(token, null, userId));
            } else {
                log.warn("Passkey authentication failed");
                return ResponseEntity.badRequest().body(
                    new AuthenticationResultDTO(null, "Authentication failed", null)
                );
            }
        } catch (RuntimeException e) {
            log.error("Error completing passkey authentication: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                new AuthenticationResultDTO(null, "Error: " + e.getMessage(), null)
            );
        }
    }

    /**
     * Check if the authenticated user has a registered passkey.
     *
     * @return status of passkey registration
     */
    @GetMapping("/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> checkPasskeyStatus() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            boolean hasPasskey = passkeyService.hasPasskeyRegistered(username);

            Map<String, Object> response = new HashMap<>();
            response.put("hasPasskey", Boolean.valueOf(hasPasskey));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error checking passkey status: {}", e.getMessage(), e);

            @NonNls Map<String, Object> response = new HashMap<>();
            response.put("error", "Failed to check passkey status: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Remove passkeys for the authenticated user.
     *
     * @return result of the passkey removal
     */
    @DeleteMapping("/deregister")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> removePasskeys() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            boolean removed = passkeyService.removePasskeys(username);

            Map<String, Object> response = new HashMap<>();
            response.put("success", Boolean.TRUE);
            response.put("removed", Boolean.valueOf(removed));
            response.put("message", removed ?
                "Passkeys removed successfully" : "No passkeys found to remove");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error removing passkeys: {}", e.getMessage(), e);

            @NonNls Map<String, Object> response = new HashMap<>();
            response.put("success", Boolean.FALSE);
            response.put("message", "Failed to remove passkeys: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}
