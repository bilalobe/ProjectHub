package com.projecthub.base.shared.events;

/**
 * Base interface for all event publishers in the system.
 * Provides a standard way to publish domain events.
 *
 * @param <T> the type of domain event to be published
 */
public interface EventPublisher<T extends DomainEvent> {
    void publish(T event);
}