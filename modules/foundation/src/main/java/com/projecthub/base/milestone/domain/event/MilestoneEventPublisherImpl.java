package com.projecthub.base.milestone.domain.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class MilestoneEventPublisherImpl implements MilestoneEventPublisher {
    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(MilestoneDomainEvent event) {
        log.debug("Publishing milestone event: {}", event);
        try {
            publisher.publishEvent(event);
        } catch (Exception e) {
            log.error("Error publishing milestone event: {}", event, e);
        }
    }
}
