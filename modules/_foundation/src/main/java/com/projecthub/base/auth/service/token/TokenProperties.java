package com.projecthub.base.auth.service.token;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.token")
public record TokenProperties(
    int accessTokenValidityMinutes,
    int refreshTokenValidityDays,
    int maxActiveTokens,
    int cleanupIntervalHours
) {
    public static final TokenProperties DEFAULT = new TokenProperties(30, 7, 5, 24);
}
