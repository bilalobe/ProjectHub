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
    private boolean reuseRefreshTokens = false;
    private int tokenRotationGracePeriodSeconds = 30;

    // Getters and setters
    public int getAccessTokenValidityMinutes() {
        return accessTokenValidityMinutes;
    }

    public void setAccessTokenValidityMinutes(int value) {
        this.accessTokenValidityMinutes = value;
    }

    public int getRefreshTokenValidityDays() {
        return refreshTokenValidityDays;
    }

    public void setRefreshTokenValidityDays(int value) {
        this.refreshTokenValidityDays = value;
    }

    public int getMaxActiveTokensPerUser() {
        return maxActiveTokensPerUser;
    }

    public void setMaxActiveTokensPerUser(int value) {
        this.maxActiveTokensPerUser = value;
    }

    public boolean isRotateRefreshTokens() {
        return rotateRefreshTokens;
    }

    public void setRotateRefreshTokens(boolean value) {
        this.rotateRefreshTokens = value;
    }

    public boolean isReuseRefreshTokens() {
        return reuseRefreshTokens;
    }

    public void setReuseRefreshTokens(boolean value) {
        this.reuseRefreshTokens = value;
    }

    public int getTokenRotationGracePeriodSeconds() {
        return tokenRotationGracePeriodSeconds;
    }

    public void setTokenRotationGracePeriodSeconds(int value) {
        this.tokenRotationGracePeriodSeconds = value;
    }
}
