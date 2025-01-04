package com.projecthub.base.component.domain.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class ComponentEventPublisherImpl implements ComponentEventPublisher {
    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(ComponentDomainEvent event) {
        log.debug("Publishing event: {}", event);
        try {
            publisher.publishEvent(event);
        } catch (Exception e) {
            log.error("Error publishing event: {}", event, e);
        }
    }
}
