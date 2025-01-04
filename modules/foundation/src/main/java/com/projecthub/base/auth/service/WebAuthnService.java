package com.projecthub.base.auth.service;

import com.projecthub.base.auth.domain.entity.PasskeyCredential;
import com.projecthub.base.auth.domain.entity.SecurityUser;
import com.projecthub.base.auth.domain.repository.PasskeyCredentialRepository;
import com.yubico.webauthn.*;
import com.yubico.webauthn.data.*;
import com.yubico.webauthn.exception.AssertionFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebAuthnService {
    private final RelyingParty relyingParty;
    private final PasskeyCredentialRepository credentialRepository;

    @Transactional
    public PublicKeyCredentialCreationOptions startRegistration(SecurityUser user) {
        try {
            log.debug("Starting WebAuthn registration for user: {}", user.getUsername());

            return relyingParty.startRegistration(
                StartRegistrationOptions.builder()
                    .user(UserIdentity.builder()
                        .name(user.getUsername())
                        .displayName(user.getUsername())
                        .id(new ByteArray(user.id().toString().getBytes()))
                        .build())
                    .authenticatorSelection(AuthenticatorSelectionCriteria.builder()
                        .residentKey(ResidentKeyRequirement.PREFERRED)
                        .userVerification(UserVerificationRequirement.PREFERRED)
                        .authenticatorAttachment(AuthenticatorAttachment.CROSS_PLATFORM)
                        .build())
                    .build());
        } catch (Exception e) {
            log.error("Failed to start WebAuthn registration", e);
            throw new WebAuthnRegistrationException("Failed to start registration", e);
        }
    }

    @Transactional
    public void finishRegistration(SecurityUser user, RegistrationResult result) {
        try {
            log.debug("Finishing WebAuthn registration for user: {}", user.getUsername());

            PasskeyCredential credential = new PasskeyCredential();
            credential.setUser(user);
            credential.setCredentialId(result.getKeyId().getId().getBase64());
            credential.setPublicKey(result.getPublicKeyCose().getBase64());
            credential.setSignatureCount(result.getSignatureCount());
            credential.setRegistrationTime(LocalDateTime.now());
            credential.setLastUsedTime(LocalDateTime.now());

            credentialRepository.save(credential);
            log.info("WebAuthn registration completed for user: {}", user.getUsername());
        } catch (Exception e) {
            log.error("Failed to complete WebAuthn registration", e);
            throw new WebAuthnRegistrationException("Failed to complete registration", e);
        }
    }

    @Transactional(readOnly = true)
    public AssertionRequest startAuthentication(String username) {
        try {
            log.debug("Starting WebAuthn authentication for user: {}", username);

            return relyingParty.startAssertion(
                StartAssertionOptions.builder()
                    .username(username)
                    .userVerification(UserVerificationRequirement.PREFERRED)
                    .build());
        } catch (Exception e) {
            log.error("Failed to start WebAuthn authentication", e);
            throw new WebAuthnAuthenticationException("Failed to start authentication", e);
        }
    }

    @Transactional
    public AssertionResult finishAuthentication(AssertionRequest request, AuthenticatorAssertionResponse response) {
        try {
            log.debug("Finishing WebAuthn authentication");

            AssertionResult result = relyingParty.finishAssertion(null);

            if (result.isSuccess()) {
                updateSignatureCount(result);
                log.info("WebAuthn authentication successful for user: {}", result.getUsername());
            }

            return result;
        } catch (AssertionFailedException e) {
            log.error("WebAuthn authentication failed", e);
            throw new WebAuthnAuthenticationException("Authentication failed", e);
        }
    }

    private void updateSignatureCount(AssertionResult result) {
        credentialRepository.findByCredentialId(result.getCredential().getCredentialId())
            .ifPresent(credential -> {
                credential.setSignatureCount(result.getSignatureCount());
                credentialRepository.save(credential);
            });
    }
}

// Custom exceptions
class WebAuthnRegistrationException extends RuntimeException {
    public WebAuthnRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}

class WebAuthnAuthenticationException extends RuntimeException {
    public WebAuthnAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
