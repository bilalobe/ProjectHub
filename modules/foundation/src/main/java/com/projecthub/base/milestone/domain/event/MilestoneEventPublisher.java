package com.projecthub.base.milestone.domain.event;

import com.projecthub.base.milestone.domain.entity.Milestone;

import java.time.Instant;
import java.util.UUID;

public interface MilestoneEventPublisher {
    void publish(MilestoneDomainEvent event);

    default void publishCreated(final Milestone milestone, final UUID initiatorId) {
        this.publish(new MilestoneDomainEvent.Created(
            UUID.randomUUID(),
            milestone.getId(),
            initiatorId,
            Instant.now()
        ));
    }

    default void publishUpdated(final Milestone milestone, final UUID initiatorId) {
        this.publish(new MilestoneDomainEvent.Updated(
            UUID.randomUUID(),
            milestone.getId(),
            initiatorId,
            Instant.now()
        ));
    }

    default void publishDeleted(final UUID milestoneId, final UUID initiatorId) {
        this.publish(new MilestoneDomainEvent.Deleted(
            UUID.randomUUID(),
            milestoneId,
            initiatorId,
            Instant.now()
        ));
    }
}
