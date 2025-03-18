package com.projecthub.base.auth.service.fortress;

import com.yubico.webauthn.*;
import com.yubico.webauthn.PublicKeyCredential;
import com.yubico.webauthn.data.*;
import com.yubico.webauthn.exception.AssertionFailedException;
import com.yubico.webauthn.exception.RegistrationFailedException;
import org.apache.directory.fortress.core.AdminMgr;
import org.apache.directory.fortress.core.ReviewMgr;
import org.apache.directory.fortress.core.model.Props;
import org.apache.directory.fortress.core.model.Session;
import org.apache.directory.fortress.core.model.User;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for integrating WebAuthn passkey authentication with Apache Fortress.
 * This service provides functionality for registering and authenticating with passkeys.
 */
@Service
public class FortressPasskeyService {

    private static final Logger log = LoggerFactory.getLogger(FortressPasskeyService.class);

    private static final String PASSKEY_PROP_PREFIX = "passkey.";
    @NonNls
    private static final String PASSKEY_CREDENTIAL_ID = PASSKEY_PROP_PREFIX + "credentialId";
    @NonNls
    private static final String PASSKEY_PUBLIC_KEY = PASSKEY_PROP_PREFIX + "publicKey";

    private final RelyingParty relyingParty;
    private final AdminMgr adminManager;
    private final ReviewMgr reviewManager;

    public FortressPasskeyService(RelyingParty relyingParty,
                              AdminMgr adminManager,
                              ReviewMgr reviewManager) {
        this.relyingParty = relyingParty;
        this.adminManager = adminManager;
        this.reviewManager = reviewManager;
    }

    /**
     * Start the registration process for a new passkey.
     *
     * @param username the user to register a passkey for
     * @return registration options to be sent to the client
     */
    public PublicKeyCredentialCreationOptions startPasskeyRegistration(@NonNls String username)
        throws IllegalArgumentException {
        log.info("Starting passkey registration for user: {}", username);

        // Find the user in Fortress
        User user = reviewManager.readUser(new User(username));
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + username);
        }

        // Create a user identity for WebAuthn
        UserIdentity userIdentity = UserIdentity.builder()
            .name(username)
            .displayName(user.getFullName() != null ? user.getFullName() : username)
            .id(getUserIdBytes(user))
            .build();

        // Set authenticator selection criteria for passkey
        AuthenticatorSelectionCriteria authenticatorSelection = AuthenticatorSelectionCriteria.builder()
            .residentKey(ResidentKeyRequirement.PREFERRED)
            .userVerification(UserVerificationRequirement.PREFERRED)
            .build();

        // Create registration options
        StartRegistrationOptions options = StartRegistrationOptions.builder()
            .user(userIdentity)
            .authenticatorSelection(authenticatorSelection)
            .build();

        // Generate registration request
        PublicKeyCredentialCreationOptions registrationRequest = relyingParty.startRegistration(options);

        log.debug("Created passkey registration request for user: {}", username);
        return registrationRequest;
    }

    /**
     * Finish the registration process for a new passkey.
     *
     * @param username the user registering the passkey
     * @param credentialCreationResponse the response from the client
     * @param requestJson the original request sent to the client
     * @return true if registration was successful
     */
    public boolean finishPasskeyRegistration(String username,
                                         String credentialCreationResponse,
                                         String requestJson) throws RuntimeException {
        log.info("Finishing passkey registration for user: {}", username);

        try {
            // Parse the client response and request
            PublicKeyCredential<com.yubico.webauthn.data.ClientRegistrationExtensionOutputs> pkc =
                PublicKeyCredential.parseRegistrationResponseJson(credentialCreationResponse);

            PublicKeyCredentialCreationOptions request =
                PublicKeyCredentialCreationOptions.fromJson(requestJson);

            // Verify the registration
            FinishRegistrationOptions options = FinishRegistrationOptions.builder()
                .request(request)
                .response(pkc)
                .build();

            RegistrationResult result = relyingParty.finishRegistration(options);

            // Store the credential with the user in Fortress
            if (result.isSuccessful()) {
                User user = reviewManager.readUser(new User(username));

                // Store credential ID and public key in user properties
                Props props = new Props();
                props.setName(PASSKEY_CREDENTIAL_ID);
                props.setValue(result.getKeyId().getId().getBase64());
                user.addProperty(props);

                props = new Props();
                props.setName(PASSKEY_PUBLIC_KEY);
                props.setValue(result.getPublicKeyCose().getBase64());
                user.addProperty(props);

                // Update the user in Fortress
                adminManager.updateUser(user);

                log.info("Successfully registered passkey for user: {}", username);
                return true;
            } else {
                log.warn("Passkey registration failed for user: {}", username);
                return false;
            }

        } catch (RegistrationFailedException | IOException e) {
            log.error("Error completing passkey registration: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to register passkey", e);
        }
    }

    /**
     * Start the authentication process using a passkey.
     *
     * @return assertion options to be sent to the client
     */
    public AssertionRequest startPasskeyAuthentication() {
        log.info("Starting passkey authentication flow");

        StartAssertionOptions options = StartAssertionOptions.builder()
            .userVerification(UserVerificationRequirement.PREFERRED)
            .build();

        return relyingParty.startAssertion(options);
    }

    /**
     * Finish the authentication process using a passkey.
     *
     * @param assertionResponse the response from the client
     * @param requestJson the original request sent to the client
     * @return Fortress session if authentication was successful, empty otherwise
     */
    public Optional<Session> finishPasskeyAuthentication(String assertionResponse,
                                                     String requestJson) {
        log.info("Finishing passkey authentication");

        try {
            // Parse the client response and request
            PublicKeyCredential<com.yubico.webauthn.data.ClientAssertionExtensionOutputs> pkc =
                PublicKeyCredential.parseAssertionResponseJson(assertionResponse);

            AssertionRequest request = AssertionRequest.fromJson(requestJson);

            // Verify the assertion
            FinishAssertionOptions options = FinishAssertionOptions.builder()
                .request(request)
                .response(pkc)
                .build();

            AssertionResult result = relyingParty.finishAssertion(options);

            if (result.isSuccess()) {
                String username = result.getUsername();
                log.info("Successful passkey authentication for user: {}", username);

                // Create a Fortress session
                User user = new User(username);
                Session session = reviewManager.createSession(user, false);

                return Optional.of(session);
            } else {
                log.warn("Passkey authentication failed");
                return Optional.empty();
            }

        } catch (AssertionFailedException | IOException e) {
            log.error("Error completing passkey authentication: {}", e.getMessage(), e);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected error during passkey authentication: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Get a byte array representation of the user ID.
     *
     * @param user the Fortress user
     * @return byte array representation of the user ID
     */
    private static ByteArray getUserIdBytes(User user) {
        String userId = user.getUserId();
        if (userId == null || userId.isEmpty()) {
            userId = UUID.randomUUID().toString();
        }

        // Convert UUID to bytes
        try {
            UUID uuid = UUID.fromString(userId);
            ByteArray userIdBytes = ByteArray.fromLong(uuid.getMostSignificantBits())
                .concat(ByteArray.fromLong(uuid.getLeastSignificantBits()));
            return userIdBytes;
        } catch (IllegalArgumentException e) {
            // Not a UUID, use string bytes
            return ByteArray.fromBase64Url(Base64.getUrlEncoder().encodeToString(userId.getBytes()));
        }
    }

    /**
     * Check if a user has a registered passkey.
     *
     * @param username the username to check
     * @return true if the user has a registered passkey
     */
    public boolean hasPasskeyRegistered(String username) throws Exception {
        User user = reviewManager.readUser(new User(username));
        if (user == null) {
            return false;
        }

        return user.getProperty(PASSKEY_CREDENTIAL_ID) != null &&
               user.getProperty(PASSKEY_PUBLIC_KEY) != null;
    }

    /**
     * Remove all passkeys for a user.
     *
     * @param username the username to remove passkeys for
     * @return true if passkeys were removed
     */
    public boolean removePasskeys(String username) throws Exception {
        User user = reviewManager.readUser(new User(username));
        if (user == null) {
            return false;
        }

        boolean hadPasskey = user.getProperty(PASSKEY_CREDENTIAL_ID) != null;

        // Remove passkey properties
        user.deleteProperty(PASSKEY_CREDENTIAL_ID);
        user.deleteProperty(PASSKEY_PUBLIC_KEY);

        // Update user in Fortress
        adminManager.updateUser(user);

        log.info("Removed passkeys for user: {}", username);
        return hadPasskey;
    }
}
