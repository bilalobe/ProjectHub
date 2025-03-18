package com.projecthub.base.core.event;

import java.time.Instant;

/**
 * Base interface for all domain events in the system.
 * Contains common properties that all domain events should have.
 */
public interface DomainEvent {
    
    /**
     * Gets the unique identifier for this event.
     * 
     * @return The event ID
     */
    String getEventId();
    
    /**
     * Gets the timestamp when this event occurred.
     * 
     * @return The event timestamp
     */
    Instant getTimestamp();
    
    /**
     * Gets the type of this domain event, used for routing and handling.
     * 
     * @return The event type
     */
    String getEventType();
}