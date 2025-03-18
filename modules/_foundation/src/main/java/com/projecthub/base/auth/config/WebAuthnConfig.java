package com.projecthub.base.auth.config;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import org.apache.directory.fortress.core.model.User;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Configuration class for WebAuthn (passkey) authentication.
 */
@Configuration
public class WebAuthnConfig {

    private static final Logger log = LoggerFactory.getLogger(WebAuthnConfig.class);

    private static final String PASSKEY_PROP_PREFIX = "passkey.";
    @NonNls
    private static final String PASSKEY_CREDENTIAL_ID = PASSKEY_PROP_PREFIX + "credentialId";
    @NonNls
    private static final String PASSKEY_PUBLIC_KEY = PASSKEY_PROP_PREFIX + "publicKey";

    @Value("${webauthn.rp.id}")
    private String rpId;

    @Value("${webauthn.rp.name}")
    private String rpName;

    @Value("${webauthn.rp.origin}")
    private String rpOrigin;

    private org.apache.directory.fortress.core.ReviewMgr reviewManager;

    public WebAuthnConfig() {
    }

    @Autowired
    public WebAuthnConfig(org.apache.directory.fortress.core.ReviewMgr reviewManager) {
        this.reviewManager = reviewManager;
    }

    /**
     * Creates a WebAuthn Relying Party bean.
     * The Relying Party is responsible for handling WebAuthn operations.
     *
     * @param credentialRepository the credential repository
     * @return RelyingParty instance
     */
    @Bean
    public RelyingParty relyingParty(CredentialRepository credentialRepository) {
        RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder()
            .id(rpId)
            .name(rpName)
            .build();

        return RelyingParty.builder()
            .identity(rpIdentity)
            .credentialRepository(credentialRepository)
            .origins(Set.of(rpOrigin))
            .build();
    }

    /**
     * Creates a WebAuthn Credential Repository backed by Fortress.
     * The Credential Repository is responsible for storing and retrieving WebAuthn credentials.
     *
     * @return CredentialRepository instance
     */
    @Bean
    public CredentialRepository credentialRepository() {
        return new FortressCredentialRepository();
    }

    /**
     * Implementation of WebAuthn CredentialRepository that uses Fortress for storage.
     * This allows storing passkey credentials in the Fortress LDAP directory.
     */
    private class FortressCredentialRepository implements CredentialRepository {

        private FortressCredentialRepository() {
        }

        @Override
        public Set<com.yubico.webauthn.data.PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
            log.debug("Getting credential IDs for user: {}", username);
            try {
                User user = reviewManager.readUser(new User(username));
                if (user == null) {
                    log.warn("User not found: {}", username);
                    return Collections.emptySet();
                }

                String credentialIdBase64 = user.getProperty(PASSKEY_CREDENTIAL_ID);
                if (credentialIdBase64 == null) {
                    log.debug("No passkey found for user: {}", username);
                    return Collections.emptySet();
                }

                ByteArray credentialId = ByteArray.fromBase64(credentialIdBase64);
                Set<com.yubico.webauthn.data.PublicKeyCredentialDescriptor> result = new HashSet<>();

                result.add(com.yubico.webauthn.data.PublicKeyCredentialDescriptor.builder()
                    .id(credentialId)
                    .build());

                return result;
            } catch (RuntimeException e) {
                log.error("Error getting credential IDs: {}", e.getMessage(), e);
                return Collections.emptySet();
            }
        }

        @Override
        public Optional<String> getUsernameForCredentialId(ByteArray credentialId) {
            log.debug("Looking up username for credential ID: {}", credentialId.getBase64());

            // This is inefficient for production use as it would need to search all users
            // In a real implementation, we would use a secondary index or a custom LDAP search
            // For now, we'll just return an empty result
            log.warn("getUsernameForCredentialId is not optimally implemented - requires LDAP search");
            return Optional.empty();
        }

        @Override
        public Optional<ByteArray> getCredentialPublicKey(ByteArray credentialId) {
            log.debug("Getting public key for credential ID: {}", credentialId.getBase64());

            try {
                // Since we don't have an efficient way to look up by credential ID directly,
                // this implementation is incomplete
                // In a full implementation, we would use the result from getUsernameForCredentialId

                log.warn("getCredentialPublicKey is not optimally implemented - requires LDAP search");
                return Optional.empty();
            } catch (RuntimeException e) {
                log.error("Error getting credential public key: {}", e.getMessage(), e);
                return Optional.empty();
            }
        }

        @Override
        public void updateCredential(ByteArray credentialId, ByteArray userHandle, ByteArray publicKeyCose) {
            log.warn("Dynamic credential update not supported by this implementation");
            // This would be implemented for credential migration scenarios
            // Not needed for basic passkey authentication
        }

        @Override
        public boolean userExists(String username) {
            try {
                User user = reviewManager.readUser(new User(username));
                return user != null;
            } catch (RuntimeException e) {
                log.error("Error checking if user exists: {}", e.getMessage(), e);
                return false;
            }
        }
    }
}
