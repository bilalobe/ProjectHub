package com.projecthub.base.auth.domain.event;

import com.projecthub.base.shared.events.DomainEvent;
import com.projecthub.base.user.domain.entity.AppUser;

import java.time.Instant;
import java.util.UUID;

public class UserVerifiedEvent implements DomainEvent {
    private final UUID eventId;
    private final AppUser user;
    private final Instant occurredOn;

    public UserVerifiedEvent(final AppUser user) {
        this.eventId = UUID.randomUUID();
        this.user = user;
        this.occurredOn = Instant.now();
    }

    @Override
    public UUID getEventId() {
        return this.eventId;
    }

    @Override
    public Instant getOccurredOn() {
        return this.occurredOn;
    }

    public AppUser getUser() {
        return this.user;
    }
}
