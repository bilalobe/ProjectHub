package com.projecthub.base.school.domain.event;

import com.projecthub.base.school.domain.entity.School;
import com.projecthub.base.school.domain.enums.SchoolType;
import com.projecthub.base.school.domain.value.SchoolContact;
import com.projecthub.base.shared.infrastructure.event.DomainEventPublisher;

import java.util.UUID;

/**
 * Publisher interface for school domain events.
 */
public interface SchoolEventPublisher extends DomainEventPublisher<SchoolDomainEvent> {
    default void publishCreated(School school, UUID initiatorId) {
        publish(new SchoolDomainEvent.Created(
            generateEventId(),
            school.getId(),
            initiatorId,
            school.getName(),
            school.getType(),
            getTimestamp()
        ));
    }

    default void publishUpdated(School school, UUID initiatorId) {
        publish(new SchoolDomainEvent.Updated(
            generateEventId(),
            school.getId(),
            initiatorId,
            school.getName(),
            getTimestamp()
        ));
    }

    default void publishDeleted(UUID schoolId, UUID initiatorId) {
        publish(new SchoolDomainEvent.Deleted(
            generateEventId(),
            schoolId,
            initiatorId,
            getTimestamp()
        ));
    }

    default void publishTypeChanged(School school, SchoolType oldType, UUID initiatorId) {
        publish(new SchoolDomainEvent.TypeChanged(
            generateEventId(),
            school.getId(),
            initiatorId,
            oldType,
            school.getType(),
            getTimestamp()
        ));
    }

    default void publishCohortAdded(School school, UUID cohortId, String cohortName, UUID initiatorId) {
        publish(new SchoolDomainEvent.CohortAdded(
            generateEventId(),
            school.getId(),
            cohortId,
            initiatorId,
            cohortName,
            getTimestamp()
        ));
    }
    
    default void publishNameUpdated(School school, String oldName, UUID initiatorId) {
        publish(new SchoolDomainEvent.NameUpdated(
            generateEventId(),
            school.getId(),
            oldName,
            school.getName(),
            initiatorId,
            getTimestamp()
        ));
    }

    default void publishContactInfoUpdated(School school, SchoolContact oldContact, UUID initiatorId) {
        publish(new SchoolDomainEvent.ContactInfoUpdated(
            generateEventId(),
            school.getId(),
            oldContact,
            school.getContact(),
            initiatorId,
            getTimestamp()
        ));
    }
}
