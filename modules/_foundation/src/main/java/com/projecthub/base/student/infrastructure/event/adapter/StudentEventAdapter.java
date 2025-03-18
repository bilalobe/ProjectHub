package com.projecthub.base.student.infrastructure.event.adapter;

import com.projecthub.base.student.domain.event.StudentDomainEvent;

public interface StudentEventAdapter {
    void publish(StudentDomainEvent event);
    String getRoutingKey(StudentDomainEvent event);
    String getExchange();
}