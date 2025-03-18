package com.projecthub.base.cohort.domain.event;

import com.projecthub.base.cohort.domain.entity.Cohort;
import com.projecthub.base.shared.infrastructure.event.DomainEventPublisher;

import java.util.UUID;

/**
 * Publisher interface for cohort domain events.
 */
public interface CohortEventPublisher extends DomainEventPublisher<CohortDomainEvent> {
    default void publishCreated(Cohort cohort, UUID initiatorId) {
        publish(new CohortDomainEvent.Created(
            generateEventId(),
            cohort.getId(),
            initiatorId,
            cohort.getName(),
            getTimestamp()
        ));
    }

    default void publishUpdated(Cohort cohort, UUID initiatorId) {
        publish(new CohortDomainEvent.Updated(
            generateEventId(),
            cohort.getId(),
            initiatorId,
            cohort.getName(),
            getTimestamp()
        ));
    }

    default void publishDeleted(UUID cohortId, UUID initiatorId) {
        publish(new CohortDomainEvent.Deleted(
            generateEventId(),
            cohortId,
            initiatorId,
            getTimestamp()
        ));
    }

    default void publishArchived(UUID cohortId, UUID initiatorId) {
        publish(new CohortDomainEvent.Archived(
            generateEventId(),
            cohortId,
            initiatorId,
            getTimestamp()
        ));
    }

    default void publishStudentAdded(Cohort cohort, UUID studentId, String studentName, UUID initiatorId) {
        publish(new CohortDomainEvent.StudentAdded(
            generateEventId(),
            cohort.getId(),
            initiatorId,
            studentId,
            studentName,
            getTimestamp()
        ));
    }

    default void publishStudentRemoved(Cohort cohort, UUID studentId, UUID initiatorId) {
        publish(new CohortDomainEvent.StudentRemoved(
            generateEventId(),
            cohort.getId(),
            initiatorId,
            studentId,
            getTimestamp()
        ));
    }
}
