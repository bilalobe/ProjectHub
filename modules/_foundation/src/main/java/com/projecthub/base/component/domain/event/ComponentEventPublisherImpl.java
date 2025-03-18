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
    public void publish(final ComponentDomainEvent event) {
        ComponentEventPublisherImpl.log.debug("Publishing event: {}", event);
        try {
            this.publisher.publishEvent(event);
        } catch (final RuntimeException e) {
            ComponentEventPublisherImpl.log.error("Error publishing event: {}", event, e);
        }
    }
}
