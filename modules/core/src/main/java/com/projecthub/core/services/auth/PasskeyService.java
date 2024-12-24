package com.projecthub.core.services.auth;

import com.projecthub.core.entities.AppUser;
import com.projecthub.core.entities.PasskeyCredential;
import com.projecthub.core.repositories.PasskeyCredentialRepository;
import org.springframework.security.webauthn.WebAuthnAuthenticationRequest;
import org.springframework.security.webauthn.WebAuthnRegistrationRequest;
import org.springframework.security.webauthn.WebAuthnRegistrationResponse;
import org.springframework.security.webauthn.WebAuthnService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PasskeyService {

    private static final String RP_ID = "projecthub.com"; // Your domain
    private static final String RP_NAME = "ProjectHub";

    private final PasskeyCredentialRepository passkeyRepository;
    private final WebAuthnService webAuthnService;
    private final SecureRandom random;


    public PasskeyService(PasskeyCredentialRepository passkeyRepository, WebAuthnService webAuthnService) {
        this.passkeyRepository = passkeyRepository;
        this.webAuthnService = webAuthnService;
        this.random = new SecureRandom();
    }

    private String generateChallenge() {
        byte[] challengeBytes = new byte[32];
        random.nextBytes(challengeBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(challengeBytes);
    }

    public WebAuthnRegistrationRequest startRegistration(AppUser user) {
        String challenge = generateChallenge();
        return WebAuthnRegistrationRequest.builder()
                .rpId(RP_ID)
                .rpName(RP_NAME)
                .userId(user.getId().toString())
                .userName(user.getUsername())
                .userDisplayName(user.getUsername())
                .challenge(challenge)
                .build();
    }

    @Transactional
    public void finishRegistration(AppUser user, WebAuthnRegistrationResponse response) {
        webAuthnService.validateRegistration(response);
        PasskeyCredential credential = new PasskeyCredential();
        credential.setUser(user);
        credential.setCredentialId(response.getCredentialId());
        credential.setPublicKey(response.getPublicKey());
        credential.setAaguid(response.getAaguid());
        credential.setSignatureCount(response.getSignatureCount());
        credential.setRegistrationTime(LocalDateTime.now());
        credential.setLastUsedTime(LocalDateTime.now());
        credential.setDeviceName(response.getDeviceName());
        passkeyRepository.save(credential);
    }

    public WebAuthnAuthenticationRequest startAuthentication(String username) {
        String challenge = generateChallenge();
        List<String> allowCredentials = passkeyRepository
                .findAllByUserUsername(username)
                .stream()
                .map(PasskeyCredential::getCredentialId)
                .collect(Collectors.toList());
        return WebAuthnAuthenticationRequest.builder()
                .rpId(RP_ID)
                .challenge(challenge)
                .allowCredentials(allowCredentials)
                .build();
    }

    @Transactional
    public boolean verifyAuthentication(WebAuthnAuthenticationRequest request) {
        Optional<PasskeyCredential> credentialOpt = passkeyRepository.findByCredentialId(request.getCredentialId());
        if (credentialOpt.isEmpty()) {
            return false;
        }
        PasskeyCredential credential = credentialOpt.get();
        try {
            webAuthnService.validateAuthentication(request, credential.getPublicKey(), credential.getSignatureCount());
            credential.setSignatureCount(request.getSignatureCount());
            credential.setLastUsedTime(LocalDateTime.now());
            passkeyRepository.save(credential);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}