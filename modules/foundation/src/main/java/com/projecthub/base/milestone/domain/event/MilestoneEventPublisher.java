package com.projecthub.base.milestone.domain.event;

import com.projecthub.base.milestone.domain.entity.Milestone;

import java.time.Instant;
import java.util.UUID;

public interface MilestoneEventPublisher {
    void publish(MilestoneDomainEvent event);

    default void publishCreated(Milestone milestone, UUID initiatorId) {
        publish(new MilestoneDomainEvent.Created(
            UUID.randomUUID(),
            milestone.getId(),
            initiatorId,
            Instant.now()
        ));
    }

    default void publishUpdated(Milestone milestone, UUID initiatorId) {
        publish(new MilestoneDomainEvent.Updated(
            UUID.randomUUID(),
            milestone.getId(),
            initiatorId,
            Instant.now()
        ));
    }

    default void publishDeleted(UUID milestoneId, UUID initiatorId) {
        publish(new MilestoneDomainEvent.Deleted(
            UUID.randomUUID(),
            milestoneId,
            initiatorId,
            Instant.now()
        ));
    }
}
