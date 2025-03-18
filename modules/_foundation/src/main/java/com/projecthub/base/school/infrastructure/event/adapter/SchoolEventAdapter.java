package com.projecthub.base.school.infrastructure.event.adapter;

import com.projecthub.base.school.domain.event.SchoolDomainEvent;

/**
 * Adapter interface for publishing school-related domain events
 */
public interface SchoolEventAdapter {
    /**
     * Publishes a school domain event
     *
     * @param event the event to publish
     */
    void publish(SchoolDomainEvent event);
}
