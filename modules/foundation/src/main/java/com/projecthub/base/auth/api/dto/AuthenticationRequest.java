package com.projecthub.base.auth.api.dto;

import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;

import java.util.List;

public record AuthenticationRequest(
    String rpId,
    String challenge,
    List<String> allowCredentials,
    PublicKeyCredentialRequestOptions options
) {
}
