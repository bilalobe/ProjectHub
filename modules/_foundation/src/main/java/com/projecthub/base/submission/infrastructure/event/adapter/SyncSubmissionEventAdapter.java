package com.projecthub.base.submission.infrastructure.event.adapter;

import com.projecthub.base.shared.exception.EventPublishingException;
import com.projecthub.base.submission.domain.event.SubmissionEvent;
import com.projecthub.base.submission.domain.event.SubmissionEventPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component("syncSubmissionEventAdapter")
@RequiredArgsConstructor
public class SyncSubmissionEventAdapter implements SubmissionEventPort {
    private final ApplicationEventPublisher publisher;


    @Override
    public void publish(SubmissionEvent event) {
         if (event == null) {
            throw new EventPublishingException("Event cannot be null");
        }
        try {
            log.debug("Publishing sync event of type {}: {}", getEventType(event), event);
             publisher.publishEvent(event);
             log.debug("Successfully published sync event: {}", event);
        } catch (RuntimeException e) {
             log.error("Failed to publish sync event: {}", event, e);
            throw new EventPublishingException("Failed to publish event: " + e.getMessage(), e);
         }
    }
       private static String getEventType(SubmissionEvent event) {
        return switch (event) {
            case SubmissionEvent.SubmissionCreated e -> "CREATED";
            case SubmissionEvent.SubmissionUpdated e -> "UPDATED";
             case SubmissionEvent.SubmissionDeleted e -> "DELETED";
        };
    }
}
