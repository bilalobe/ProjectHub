package com.projecthub.base.user.domain.event;

import com.projecthub.base.shared.domain.enums.security.RoleStatus;
import com.projecthub.base.shared.events.DomainEvent;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public sealed interface UserDomainEvent extends DomainEvent permits
    UserDomainEvent.Created,
    UserDomainEvent.ProfileUpdated,
    UserDomainEvent.AvatarUpdated,
    UserDomainEvent.StatusUpdated,
    UserDomainEvent.RolesChanged,
    UserDomainEvent.AccountStatusChanged,
    UserDomainEvent.Verified,
    UserDomainEvent.Deleted {

    UUID getUserId();
    UUID getInitiatorId();

    record Created(
        UUID eventId,
        UUID userId,
        UUID initiatorId,
        String username,
        String email,
        Set<String> roles,
        Instant occurredOn
    ) implements UserDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }

        @Override
        public UUID getInitiatorId() {
            return initiatorId;
        }
    }

    record ProfileUpdated(
        UUID eventId,
        UUID userId,
        UUID initiatorId,
        String firstName,
        String lastName,
        String email,
        Instant occurredOn
    ) implements UserDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }

        @Override
        public UUID getInitiatorId() {
            return initiatorId;
        }
    }

    record AvatarUpdated(
        UUID eventId,
        UUID userId,
        UUID initiatorId,
        String avatarUrl,
        Instant occurredOn
    ) implements UserDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }

        @Override
        public UUID getInitiatorId() {
            return initiatorId;
        }
    }

    record StatusUpdated(
        UUID eventId,
        UUID userId,
        UUID initiatorId,
        String statusMessage,
        Instant occurredOn
    ) implements UserDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }

        @Override
        public UUID getInitiatorId() {
            return initiatorId;
        }
    }

    record RolesChanged(
        UUID eventId,
        UUID userId,
        UUID initiatorId,
        Set<String> oldRoles,
        Set<String> newRoles,
        Instant occurredOn
    ) implements UserDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }

        @Override
        public UUID getInitiatorId() {
            return initiatorId;
        }
    }

    record AccountStatusChanged(
        UUID eventId,
        UUID userId,
        UUID initiatorId,
        RoleStatus oldStatus,
        RoleStatus newStatus,
        String reason,
        Instant occurredOn
    ) implements UserDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }

        @Override
        public UUID getInitiatorId() {
            return initiatorId;
        }
    }

    record Verified(
        UUID eventId,
        UUID userId,
        UUID initiatorId,
        Instant occurredOn
    ) implements UserDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }

        @Override
        public UUID getInitiatorId() {
            return initiatorId;
        }
    }

    record Deleted(
        UUID eventId,
        UUID userId,
        UUID initiatorId,
        Instant occurredOn
    ) implements UserDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }

        @Override
        public UUID getInitiatorId() {
            return initiatorId;
        }
    }
}