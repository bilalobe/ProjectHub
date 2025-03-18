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
    public void publish(final MilestoneDomainEvent event) {
        MilestoneEventPublisherImpl.log.debug("Publishing milestone event: {}", event);
        try {
            this.publisher.publishEvent(event);
        } catch (final RuntimeException e) {
            MilestoneEventPublisherImpl.log.error("Error publishing milestone event: {}", event, e);
        }
    }
}
