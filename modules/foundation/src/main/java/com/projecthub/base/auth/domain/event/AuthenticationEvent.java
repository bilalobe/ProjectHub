package com.projecthub.base.auth.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuthenticationEvent(
    UUID userId,
    String username,
    String ipAddress,
    AuthenticationEventType eventType,
    LocalDateTime timestamp,
    String details
) {
    public static AuthenticationEvent createLoginSuccess(final UUID userId, final String username, final String ipAddress) {
        return new AuthenticationEvent(userId, username, ipAddress,
            AuthenticationEventType.LOGIN_SUCCESS, LocalDateTime.now(), null);
    }

    public static AuthenticationEvent createLoginFailure(final String username, final String ipAddress, final String details) {
        return new AuthenticationEvent(null, username, ipAddress,
            AuthenticationEventType.LOGIN_FAILURE, LocalDateTime.now(), details);
    }

    public static AuthenticationEvent createLogout(final UUID userId, final String username, final String ipAddress) {
        return new AuthenticationEvent(userId, username, ipAddress,
            AuthenticationEventType.LOGOUT, LocalDateTime.now(), null);
    }

    public static AuthenticationEvent createTokenRefresh(final UUID userId, final String username, final String ipAddress) {
        return new AuthenticationEvent(userId, username, ipAddress,
            AuthenticationEventType.TOKEN_REFRESH, LocalDateTime.now(), null);
    }

    public static AuthenticationEvent createAccountLocked(final String username, final String ipAddress, final String reason) {
        return new AuthenticationEvent(null, username, ipAddress,
            AuthenticationEventType.ACCOUNT_LOCKED, LocalDateTime.now(), reason);
    }

    public static AuthenticationEvent createSuspiciousActivity(final String username, final String ipAddress, final String details) {
        return new AuthenticationEvent(null, username, ipAddress,
            AuthenticationEventType.SUSPICIOUS_ACTIVITY, LocalDateTime.now(), details);
    }

    public String routingKey() {
        return "auth." + this.eventType.name().toLowerCase();
    }

    public enum AuthenticationEventType {
        LOGIN_SUCCESS,
        LOGIN_FAILURE,
        LOGOUT,
        TOKEN_REFRESH,
        ACCOUNT_LOCKED,
        SUSPICIOUS_ACTIVITY
    }
}
