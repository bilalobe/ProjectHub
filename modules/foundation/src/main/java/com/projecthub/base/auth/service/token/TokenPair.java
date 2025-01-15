package com.projecthub.base.auth.service.token;

public record TokenPair(
    String accessToken,
    String refreshToken,
    Long expiresIn
) {
    public TokenPair {
        if (null == accessToken || null == refreshToken) {
            throw new IllegalArgumentException("Tokens cannot be null");
        }
        if (0 >= expiresIn) {
            throw new IllegalArgumentException("ExpiresIn must be positive");
        }
    }
}
