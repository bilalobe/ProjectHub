package com.projecthub.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "auth.token")
public class TokenConfigImpl implements TokenConfig {
    private int accessTokenValidityMinutes = 30;
    private int refreshTokenValidityDays = 7;
    private int maxActiveTokensPerUser = 5;
    private boolean rotateRefreshTokens = true;
    private boolean reuseRefreshTokens;
    private int tokenRotationGracePeriodSeconds = 30;

    // Getters and setters
    @Override
    public int getAccessTokenValidityMinutes() {
        return this.accessTokenValidityMinutes;
    }

    @Override
    public void setAccessTokenValidityMinutes(final int value) {
        accessTokenValidityMinutes = value;
    }

    @Override
    public int getRefreshTokenValidityDays() {
        return this.refreshTokenValidityDays;
    }

    @Override
    public void setRefreshTokenValidityDays(final int value) {
        refreshTokenValidityDays = value;
    }

    @Override
    public int getMaxActiveTokensPerUser() {
        return this.maxActiveTokensPerUser;
    }

    @Override
    public void setMaxActiveTokensPerUser(final int value) {
        maxActiveTokensPerUser = value;
    }

    @Override
    public boolean isRotateRefreshTokens() {
        return this.rotateRefreshTokens;
    }

    @Override
    public void setRotateRefreshTokens(final boolean value) {
        rotateRefreshTokens = value;
    }

    @Override
    public boolean isReuseRefreshTokens() {
        return this.reuseRefreshTokens;
    }

    @Override
    public void setReuseRefreshTokens(final boolean value) {
        reuseRefreshTokens = value;
    }

    @Override
    public int getTokenRotationGracePeriodSeconds() {
        return this.tokenRotationGracePeriodSeconds;
    }

    @Override
    public void setTokenRotationGracePeriodSeconds(final int value) {
        tokenRotationGracePeriodSeconds = value;
    }
}