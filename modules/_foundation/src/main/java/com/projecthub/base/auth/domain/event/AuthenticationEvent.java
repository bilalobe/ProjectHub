package com.projecthub.base.auth.domain.event;

import com.projecthub.base.shared.events.DomainEvent;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

public record AuthenticationEvent(
    UUID eventId,
    String username,
    UUID userId,
    AuthEventType eventType,
    String ipAddress,
    String details,
    Instant occurredOn
) implements DomainEvent {

    public AuthenticationEvent(String username, UUID userId, AuthEventType eventType, String ipAddress) {
        this(UUID.randomUUID(), username, userId, eventType, ipAddress, null, Instant.now());
    }

    public AuthenticationEvent(String username, UUID userId, AuthEventType eventType, String ipAddress, String details) {
        this(UUID.randomUUID(), username, userId, eventType, ipAddress, details, Instant.now());
    }

    public static AuthenticationEvent createLoginSuccess(final UUID userId, final String username, final String ipAddress) {
        return new AuthenticationEvent(username, userId, AuthEventType.LOGIN_SUCCESS, ipAddress);
    }

    public static AuthenticationEvent createLoginFailure(final String username, final String ipAddress, final String details) {
        return new AuthenticationEvent(username, null, AuthEventType.LOGIN_FAILURE, ipAddress, details);
    }

    public static AuthenticationEvent createLogout(final UUID userId, final String username, final String ipAddress) {
        return new AuthenticationEvent(username, userId, AuthEventType.LOGOUT, ipAddress);
    }

    public static AuthenticationEvent createTokenRefresh(final UUID userId, final String username, final String ipAddress) {
        return new AuthenticationEvent(username, userId, AuthEventType.TOKEN_REFRESH, ipAddress);
    }

    public static AuthenticationEvent createAccountLocked(final String username, final String ipAddress, final String reason) {
        return new AuthenticationEvent(username, null, AuthEventType.ACCOUNT_LOCKED, ipAddress, reason);
    }

    public static AuthenticationEvent createSuspiciousActivity(final String username, final String ipAddress, final String details) {
        return new AuthenticationEvent(username, null, AuthEventType.SUSPICIOUS_ACTIVITY, ipAddress, details);
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public Instant getOccurredOn() {
        return occurredOn;
    }

    @NonNls
    public String routingKey() {
        return "auth." + this.eventType.name().toLowerCase(Locale.ROOT);
    }

    public enum AuthEventType {
        LOGIN_SUCCESS,
        LOGIN_FAILURE,
        LOGOUT,
        TOKEN_REFRESH,
        ACCOUNT_LOCKED,
        SUSPICIOUS_ACTIVITY
    }
}
