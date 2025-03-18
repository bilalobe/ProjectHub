package com.projecthub.base.shared.domain.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class ActivityEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public ActivityEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishActivity(
            String userId,
            ActivityType activityType,
            String entityType,
            String entityId,
            Map<String, Object> metadata
    ) {
        ActivityEvent event = new ActivityEvent(
            userId,
            activityType,
            entityType,
            entityId,
            metadata,
            Instant.now()
        );
        eventPublisher.publishEvent(event);
    }
}