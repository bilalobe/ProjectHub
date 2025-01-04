package com.projecthub.base.auth.service.token;

public record TokenPair(
    String accessToken,
    String refreshToken,
    Long expiresIn
) {
    public TokenPair {
        if (accessToken == null || refreshToken == null) {
            throw new IllegalArgumentException("Tokens cannot be null");
        }
        if (expiresIn <= 0) {
            throw new IllegalArgumentException("ExpiresIn must be positive");
        }
    }
}
