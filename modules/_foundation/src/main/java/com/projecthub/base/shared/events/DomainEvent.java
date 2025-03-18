package com.projecthub.base.shared.events;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {
    UUID getEventId();

    Instant getOccurredOn();
}
