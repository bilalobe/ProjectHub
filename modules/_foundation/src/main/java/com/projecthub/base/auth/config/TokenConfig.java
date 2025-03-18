package com.projecthub.base.auth.config;

public interface TokenConfig {
    int getAccessTokenValidityMinutes();
    void setAccessTokenValidityMinutes(int value);
    int getRefreshTokenValidityDays();
    void setRefreshTokenValidityDays(int value);
    int getMaxActiveTokensPerUser();
    void setMaxActiveTokensPerUser(int value);
    boolean isRotateRefreshTokens();
    void setRotateRefreshTokens(boolean value);
    boolean isReuseRefreshTokens();
    void setReuseRefreshTokens(boolean value);
    int getTokenRotationGracePeriodSeconds();
    void setTokenRotationGracePeriodSeconds(int value);
}
