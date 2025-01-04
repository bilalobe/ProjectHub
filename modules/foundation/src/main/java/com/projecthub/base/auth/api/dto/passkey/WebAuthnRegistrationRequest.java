package com.projecthub.base.auth.api.dto.passkey;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WebAuthnRegistrationRequest {
    String rpId;
    String rpName;
    String userId;
    String userName;
    String userDisplayName;
    String challenge;
}
