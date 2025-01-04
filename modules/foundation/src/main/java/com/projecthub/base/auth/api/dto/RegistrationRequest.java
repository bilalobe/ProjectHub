package com.projecthub.base.auth.api.dto;

import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;

public record RegistrationRequest(
    String rpId,
    String rpName,
    String userId,
    String userName,
    String challenge,
    PublicKeyCredentialCreationOptions options
) {
}
