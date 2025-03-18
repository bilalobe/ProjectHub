package com.projecthub.base.student.domain.event;

public interface StudentEventPublisher {
    void publish(StudentDomainEvent event);
}
