package com.projecthub.base.shared.domain.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class NotificationEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public NotificationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishNotification(
            String recipientId,
            String senderId,
            NotificationType type,
            String title,
            String message,
            Map<String, Object> metadata
    ) {
        NotificationEvent event = new NotificationEvent(
            recipientId,
            senderId,
            type,
            title,
            message,
            metadata,
            Instant.now(),
            false
        );
        eventPublisher.publishEvent(event);
    }
}