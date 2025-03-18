package com.projecthub.base.shared.infrastructure.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Base interface for domain event publishers.
 * @param <T> The type of domain event
 */
public interface DomainEventPublisher<T> {
    void publish(T event);
    
    String getExchange();
    
    String getRoutingKey(T event);
    
    default UUID generateEventId() {
        return UUID.randomUUID();
    }
    
    default Instant getTimestamp() {
        return Instant.now();
    }
}