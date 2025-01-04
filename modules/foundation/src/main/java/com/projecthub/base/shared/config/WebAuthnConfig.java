package com.projecthub.base.shared.config;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartAssertionOptions;
import com.yubico.webauthn.data.AttestationConveyancePreference;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import com.yubico.webauthn.data.UserVerificationRequirement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.Set;

@Configuration
public class WebAuthnConfig {
    private static final String DEFAULT_RP_ID = "localhost";
    private static final String DEFAULT_RP_NAME = "ProjectHub";
    private static final String DEFAULT_ORIGIN = "http://localhost:8080";
    private static final long DEFAULT_TIMEOUT = 60000L;

    @Value("${webauthn.rp.id:" + DEFAULT_RP_ID + "}")
    private String rpId;

    @Value("${webauthn.rp.name:" + DEFAULT_RP_NAME + "}")
    private String rpName;

    @Value("${webauthn.rp.origin:" + DEFAULT_ORIGIN + "}")
    private String rpOrigin;

    @Value("${webauthn.timeout:" + DEFAULT_TIMEOUT + "}")
    private Long timeout;

    public RelyingParty relyingParty(CredentialRepository credentialRepository) {
        var rpIdentity = RelyingPartyIdentity.builder()
            .id(rpId)
            .name(rpName)
            .build();

        return RelyingParty.builder()
            .identity(rpIdentity)
            .credentialRepository(credentialRepository)
            .origins(Set.of(rpOrigin))
            .attestationConveyancePreference(AttestationConveyancePreference.DIRECT)
            .build();
    }

    @Bean
    public StartAssertionOptions assertionOptions() {
        return StartAssertionOptions.builder()
            .timeout(Optional.of(timeout))
            .userVerification(UserVerificationRequirement.PREFERRED)
            .build();
    }
}
