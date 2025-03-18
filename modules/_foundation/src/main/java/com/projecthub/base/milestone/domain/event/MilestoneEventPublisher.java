package com.projecthub.base.milestone.domain.event;

import com.projecthub.base.milestone.domain.entity.Milestone;
import com.projecthub.base.shared.infrastructure.event.DomainEventPublisher;

import java.util.UUID;

/**
 * Publisher interface for milestone domain events.
 */
public interface MilestoneEventPublisher extends DomainEventPublisher<MilestoneDomainEvent> {
    default void publishCreated(Milestone milestone, UUID initiatorId) {
        publish(new MilestoneDomainEvent.Created(
            generateEventId(),
            milestone.getId(),
            initiatorId,
            getTimestamp()
        ));
    }

    default void publishUpdated(Milestone milestone, UUID initiatorId) {
        publish(new MilestoneDomainEvent.Updated(
            generateEventId(),
            milestone.getId(),
            initiatorId,
            getTimestamp()
        ));
    }

    default void publishDeleted(UUID milestoneId, UUID initiatorId) {
        publish(new MilestoneDomainEvent.Deleted(
            generateEventId(),
            milestoneId,
            initiatorId,
            getTimestamp()
        ));
    }

    default void publishCompleted(Milestone milestone, UUID initiatorId) {
        publish(new MilestoneDomainEvent.Completed(
            generateEventId(),
            milestone.getId(),
            initiatorId,
            milestone.getStatus(),
            getTimestamp()
        ));
    }
}
