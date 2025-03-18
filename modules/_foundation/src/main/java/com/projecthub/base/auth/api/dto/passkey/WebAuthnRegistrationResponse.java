package com.projecthub.base.auth.api.dto.passkey;

import lombok.Value;

@Value
public class WebAuthnRegistrationResponse {
    String credentialId;
    String publicKey;
    String aaguid;
    Long signatureCount;
    String deviceName;
}
