package com.projecthub.base.auth.api.dto.passkey;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class WebAuthnAuthenticationRequest {
    String rpId;
    String challenge;
    List<String> allowCredentials;
    Long signatureCount;
    String credentialId;
}
