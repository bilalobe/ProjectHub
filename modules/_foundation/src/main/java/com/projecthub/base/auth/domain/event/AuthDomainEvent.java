package com.projecthub.base.auth.domain.event;

import com.projecthub.base.shared.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public sealed interface AuthDomainEvent extends DomainEvent permits
    AuthDomainEvent.UserRegistered,
    AuthDomainEvent.UserLoggedIn,
    AuthDomainEvent.UserLoggedOut,
    AuthDomainEvent.PasswordChanged,
    AuthDomainEvent.AccountLocked {

    UUID getUserId();

    record UserRegistered(
        UUID eventId,
        UUID userId,
        String username,
        Instant occurredOn
    ) implements AuthDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }

        @Override
        public UUID getUserId() {
            return userId;
        }
    }

    record UserLoggedIn(
        UUID eventId,
        UUID userId,
        String ipAddress,
        Instant occurredOn
    ) implements AuthDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }

        @Override
        public UUID getUserId() {
            return userId;
        }
    }

    record UserLoggedOut(
        UUID eventId,
        UUID userId,
        String sessionId,
        Instant occurredOn
    ) implements AuthDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }

        @Override
        public UUID getUserId() {
            return userId;
        }
    }

    record PasswordChanged(
        UUID eventId,
        UUID userId,
        boolean isReset,
        Instant occurredOn
    ) implements AuthDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }

        @Override
        public UUID getUserId() {
            return userId;
        }
    }

    record AccountLocked(
        UUID eventId,
        UUID userId,
        String reason,
        Instant occurredOn
    ) implements AuthDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }

        @Override
        public UUID getUserId() {
            return userId;
        }
    }
}