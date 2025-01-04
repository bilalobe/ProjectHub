package com.projecthub.base.auth.api.dto;

import com.yubico.webauthn.data.AuthenticatorAttestationResponse;

public record RegistrationResponse(
    String id,
    String rawId,
    AuthenticatorAttestationResponse response,
    String type
) {
}
