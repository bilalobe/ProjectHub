package com.projecthub.base.core.event;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Base implementation for all domain events.
 * Provides common functionality and properties for all events.
 */
@Getter
public abstract class BaseDomainEvent implements DomainEvent {
    private final String eventId;
    private final Instant timestamp;
    
    protected BaseDomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
    }
    
    /**
     * Gets the type of this domain event, used for routing and handling.
     * Default implementation uses the simple name of the class.
     * 
     * @return The event type
     */
    @Override
    public String getEventType() {
        return this.getClass().getSimpleName();
    }
}