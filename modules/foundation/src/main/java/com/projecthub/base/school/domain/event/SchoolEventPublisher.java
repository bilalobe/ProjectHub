package com.projecthub.base.school.domain.event;


public interface SchoolEventPublisher {
    void publish(SchoolDomainEvent event);
}
