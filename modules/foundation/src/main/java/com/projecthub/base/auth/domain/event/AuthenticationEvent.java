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
    public static AuthenticationEvent createLoginSuccess(UUID userId, String username, String ipAddress) {
        return new AuthenticationEvent(userId, username, ipAddress,
            AuthenticationEventType.LOGIN_SUCCESS, LocalDateTime.now(), null);
    }

    public static AuthenticationEvent createLoginFailure(String username, String ipAddress, String details) {
        return new AuthenticationEvent(null, username, ipAddress,
            AuthenticationEventType.LOGIN_FAILURE, LocalDateTime.now(), details);
    }

    public static AuthenticationEvent createLogout(UUID userId, String username, String ipAddress) {
        return new AuthenticationEvent(userId, username, ipAddress,
            AuthenticationEventType.LOGOUT, LocalDateTime.now(), null);
    }

    public static AuthenticationEvent createTokenRefresh(UUID userId, String username, String ipAddress) {
        return new AuthenticationEvent(userId, username, ipAddress,
            AuthenticationEventType.TOKEN_REFRESH, LocalDateTime.now(), null);
    }

    public static AuthenticationEvent createAccountLocked(String username, String ipAddress, String reason) {
        return new AuthenticationEvent(null, username, ipAddress,
            AuthenticationEventType.ACCOUNT_LOCKED, LocalDateTime.now(), reason);
    }

    public static AuthenticationEvent createSuspiciousActivity(String username, String ipAddress, String details) {
        return new AuthenticationEvent(null, username, ipAddress,
            AuthenticationEventType.SUSPICIOUS_ACTIVITY, LocalDateTime.now(), details);
    }

    public String routingKey() {
        return "auth." + eventType.name().toLowerCase();
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
