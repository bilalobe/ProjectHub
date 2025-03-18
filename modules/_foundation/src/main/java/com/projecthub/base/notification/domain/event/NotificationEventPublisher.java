package com.projecthub.base.notification.domain.event;

import com.projecthub.base.shared.infrastructure.event.DomainEventPublisher;

import java.util.UUID;

/**
 * Publisher interface for notification domain events.
 */
public interface NotificationEventPublisher extends DomainEventPublisher<NotificationDomainEvent> {
    default void publishCreated(UUID notificationId, UUID recipientId, String type, String content) {
        publish(new NotificationDomainEvent.Created(
            generateEventId(),
            notificationId,
            recipientId,
            type,
            content,
            getTimestamp()
        ));
    }

    default void publishRead(UUID notificationId, UUID recipientId) {
        publish(new NotificationDomainEvent.Read(
            generateEventId(),
            notificationId,
            recipientId,
            getTimestamp()
        ));
    }

    default void publishDeleted(UUID notificationId, UUID recipientId) {
        publish(new NotificationDomainEvent.Deleted(
            generateEventId(),
            notificationId,
            recipientId,
            getTimestamp()
        ));
    }
}