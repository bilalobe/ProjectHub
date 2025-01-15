package com.projecthub.base.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "auth.token")
public class TokenConfig {
    private int accessTokenValidityMinutes = 30;
    private int refreshTokenValidityDays = 7;
    private int maxActiveTokensPerUser = 5;
    private boolean rotateRefreshTokens = true;
    private boolean reuseRefreshTokens;
    private int tokenRotationGracePeriodSeconds = 30;

    // Getters and setters
    public int getAccessTokenValidityMinutes() {
        return this.accessTokenValidityMinutes;
    }

    public void setAccessTokenValidityMinutes(final int value) {
        accessTokenValidityMinutes = value;
    }

    public int getRefreshTokenValidityDays() {
        return this.refreshTokenValidityDays;
    }

    public void setRefreshTokenValidityDays(final int value) {
        refreshTokenValidityDays = value;
    }

    public int getMaxActiveTokensPerUser() {
        return this.maxActiveTokensPerUser;
    }

    public void setMaxActiveTokensPerUser(final int value) {
        maxActiveTokensPerUser = value;
    }

    public boolean isRotateRefreshTokens() {
        return this.rotateRefreshTokens;
    }

    public void setRotateRefreshTokens(final boolean value) {
        rotateRefreshTokens = value;
    }

    public boolean isReuseRefreshTokens() {
        return this.reuseRefreshTokens;
    }

    public void setReuseRefreshTokens(final boolean value) {
        reuseRefreshTokens = value;
    }

    public int getTokenRotationGracePeriodSeconds() {
        return this.tokenRotationGracePeriodSeconds;
    }

    public void setTokenRotationGracePeriodSeconds(final int value) {
        tokenRotationGracePeriodSeconds = value;
    }
}
