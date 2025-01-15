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
    public PublicKeyCredentialCreationOptions startRegistration(final SecurityUser user) {
        try {
            WebAuthnService.log.debug("Starting WebAuthn registration for user: {}", user.getUsername());

            return this.relyingParty.startRegistration(
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
        } catch (final Exception e) {
            WebAuthnService.log.error("Failed to start WebAuthn registration", e);
            throw new WebAuthnRegistrationException("Failed to start registration", e);
        }
    }

    @Transactional
    public void finishRegistration(final SecurityUser user, final RegistrationResult result) {
        try {
            WebAuthnService.log.debug("Finishing WebAuthn registration for user: {}", user.getUsername());

            final PasskeyCredential credential = new PasskeyCredential();
            credential.setUser(user);
            credential.setCredentialId(result.getKeyId().getId().getBase64());
            credential.setPublicKey(result.getPublicKeyCose().getBase64());
            credential.setSignatureCount(result.getSignatureCount());
            credential.setRegistrationTime(LocalDateTime.now());
            credential.setLastUsedTime(LocalDateTime.now());

            this.credentialRepository.save(credential);
            WebAuthnService.log.info("WebAuthn registration completed for user: {}", user.getUsername());
        } catch (final Exception e) {
            WebAuthnService.log.error("Failed to complete WebAuthn registration", e);
            throw new WebAuthnRegistrationException("Failed to complete registration", e);
        }
    }

    @Transactional(readOnly = true)
    public AssertionRequest startAuthentication(final String username) {
        try {
            WebAuthnService.log.debug("Starting WebAuthn authentication for user: {}", username);

            return this.relyingParty.startAssertion(
                StartAssertionOptions.builder()
                    .username(username)
                    .userVerification(UserVerificationRequirement.PREFERRED)
                    .build());
        } catch (final Exception e) {
            WebAuthnService.log.error("Failed to start WebAuthn authentication", e);
            throw new WebAuthnAuthenticationException("Failed to start authentication", e);
        }
    }

    @Transactional
    public AssertionResult finishAuthentication(final AssertionRequest request, final AuthenticatorAssertionResponse response) {
        try {
            WebAuthnService.log.debug("Finishing WebAuthn authentication");

            final AssertionResult result = this.relyingParty.finishAssertion(null);

            if (result.isSuccess()) {
                this.updateSignatureCount(result);
                WebAuthnService.log.info("WebAuthn authentication successful for user: {}", result.getUsername());
            }

            return result;
        } catch (final AssertionFailedException e) {
            WebAuthnService.log.error("WebAuthn authentication failed", e);
            throw new WebAuthnAuthenticationException("Authentication failed", e);
        }
    }

    private void updateSignatureCount(final AssertionResult result) {
        this.credentialRepository.findByCredentialId(result.getCredential().getCredentialId())
            .ifPresent(credential -> {
                credential.setSignatureCount(result.getSignatureCount());
                this.credentialRepository.save(credential);
            });
    }
}

// Custom exceptions
class WebAuthnRegistrationException extends RuntimeException {
    public WebAuthnRegistrationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

class WebAuthnAuthenticationException extends RuntimeException {
    public WebAuthnAuthenticationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
