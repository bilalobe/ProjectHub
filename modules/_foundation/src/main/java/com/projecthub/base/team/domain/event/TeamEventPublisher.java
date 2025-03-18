package com.projecthub.base.team.domain.event;

import com.projecthub.base.shared.infrastructure.event.DomainEventPublisher;
import com.projecthub.base.team.domain.entity.Team;
import com.projecthub.base.team.domain.enums.TeamStatus;

import java.util.UUID;

/**
 * Publisher interface for team domain events.
 */
public interface TeamEventPublisher extends DomainEventPublisher<TeamDomainEvent> {
    default void publishCreated(Team team, UUID initiatorId) {
        publish(new TeamDomainEvent.Created(
            generateEventId(),
            team.getId(),
            initiatorId,
            team.getName(),
            getTimestamp()
        ));
    }

    default void publishUpdated(Team team, UUID initiatorId) {
        publish(new TeamDomainEvent.Updated(
            generateEventId(),
            team.getId(),
            initiatorId,
            team.getName(),
            getTimestamp()
        ));
    }

    default void publishDeleted(UUID teamId, UUID initiatorId) {
        publish(new TeamDomainEvent.Deleted(
            generateEventId(),
            teamId,
            initiatorId,
            getTimestamp()
        ));
    }

    default void publishStatusChanged(Team team, TeamStatus oldStatus, UUID initiatorId) {
        publish(new TeamDomainEvent.StatusChanged(
            generateEventId(),
            team.getId(),
            initiatorId,
            oldStatus,
            team.getStatus(),
            getTimestamp()
        ));
    }

    default void publishMemberAdded(Team team, UUID memberId, String memberName, UUID initiatorId) {
        publish(new TeamDomainEvent.MemberAdded(
            generateEventId(),
            team.getId(),
            initiatorId,
            memberId,
            memberName,
            getTimestamp()
        ));
    }

    default void publishMemberRemoved(Team team, UUID memberId, UUID initiatorId) {
        publish(new TeamDomainEvent.MemberRemoved(
            generateEventId(),
            team.getId(),
            initiatorId,
            memberId,
            getTimestamp()
        ));
    }

    default void publishAssignedToCohort(Team team, UUID cohortId, UUID initiatorId) {
        publish(new TeamDomainEvent.AssignedToCohort(
            generateEventId(),
            team.getId(),
            initiatorId,
            cohortId,
            getTimestamp()
        ));
    }
}