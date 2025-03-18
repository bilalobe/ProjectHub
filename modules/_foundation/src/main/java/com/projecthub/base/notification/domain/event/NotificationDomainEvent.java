package com.projecthub.base.notification.domain.event;

import com.projecthub.base.shared.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public sealed interface NotificationDomainEvent extends DomainEvent permits
    NotificationDomainEvent.Created,
    NotificationDomainEvent.Read,
    NotificationDomainEvent.Deleted {

    UUID notificationId();
    UUID getRecipientId();

    record Created(
        UUID eventId,
        UUID notificationId,
        UUID recipientId,
        String type,
        String content,
        Instant occurredOn
    ) implements NotificationDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }
    }

    record Read(
        UUID eventId,
        UUID notificationId,
        UUID recipientId,
        Instant occurredOn
    ) implements NotificationDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }
    }

    record Deleted(
        UUID eventId,
        UUID notificationId,
        UUID recipientId,
        Instant occurredOn
    ) implements NotificationDomainEvent {
        @Override
        public UUID getEventId() {
            return eventId;
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn;
        }
    }
}