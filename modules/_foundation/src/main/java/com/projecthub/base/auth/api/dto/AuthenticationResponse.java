package com.projecthub.base.auth.api.dto;

import com.yubico.webauthn.data.AuthenticatorAssertionResponse;

public record AuthenticationResponse(
    String id,
    String rawId,
    AuthenticatorAssertionResponse response,
    String type
) {
}
