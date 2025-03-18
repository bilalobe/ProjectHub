package com.projecthub.base.user.domain.event;

import com.projecthub.base.shared.domain.enums.security.RoleStatus;
import com.projecthub.base.shared.infrastructure.event.DomainEventPublisher;
import com.projecthub.base.user.api.dto.AppUserDTO;
import com.projecthub.base.user.api.dto.AppUserProfileDTO;

import java.util.Set;
import java.util.UUID;

/**
 * Publisher interface for user domain events.
 */
public interface UserEventPublisher extends DomainEventPublisher<UserDomainEvent> {
    default void publishCreated(AppUserDTO user, UUID initiatorId) {
        publish(new UserDomainEvent.Created(
            generateEventId(),
            user.id(),
            initiatorId,
            user.username(),
            user.email(),
            user.roles(),
            getTimestamp()
        ));
    }

    default void publishProfileUpdated(AppUserProfileDTO profile, UUID initiatorId) {
        publish(new UserDomainEvent.ProfileUpdated(
            generateEventId(),
            profile.id(),
            initiatorId,
            profile.firstName(),
            profile.lastName(),
            profile.email(),
            getTimestamp()
        ));
    }

    default void publishAvatarUpdated(UUID userId, String avatarUrl, UUID initiatorId) {
        publish(new UserDomainEvent.AvatarUpdated(
            generateEventId(),
            userId,
            initiatorId,
            avatarUrl,
            getTimestamp()
        ));
    }

    default void publishStatusUpdated(UUID userId, String statusMessage, UUID initiatorId) {
        publish(new UserDomainEvent.StatusUpdated(
            generateEventId(),
            userId,
            initiatorId,
            statusMessage,
            getTimestamp()
        ));
    }

    default void publishRolesChanged(UUID userId, Set<String> oldRoles, Set<String> newRoles, UUID initiatorId) {
        publish(new UserDomainEvent.RolesChanged(
            generateEventId(),
            userId,
            initiatorId,
            oldRoles,
            newRoles,
            getTimestamp()
        ));
    }

    default void publishAccountStatusChanged(UUID userId, RoleStatus oldStatus, RoleStatus newStatus, String reason, UUID initiatorId) {
        publish(new UserDomainEvent.AccountStatusChanged(
            generateEventId(),
            userId,
            initiatorId,
            oldStatus,
            newStatus,
            reason,
            getTimestamp()
        ));
    }

    default void publishVerified(UUID userId, UUID initiatorId) {
        publish(new UserDomainEvent.Verified(
            generateEventId(),
            userId,
            initiatorId,
            getTimestamp()
        ));
    }

    default void publishDeleted(UUID userId, UUID initiatorId) {
        publish(new UserDomainEvent.Deleted(
            generateEventId(),
            userId,
            initiatorId,
            getTimestamp()
        ));
    }
}