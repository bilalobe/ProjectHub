package com.projecthub.base.student.infrastructure.event;

import com.projecthub.base.student.domain.event.StudentDomainEvent;
import com.projecthub.base.student.domain.event.StudentEventPublisher;
import com.projecthub.base.student.infrastructure.event.adapter.AsyncStudentEventAdapter;
import com.projecthub.base.student.infrastructure.event.adapter.SyncStudentEventAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncStudentEventPublisher implements StudentEventPublisher {
    private final AsyncStudentEventAdapter asyncAdapter;
    private final SyncStudentEventAdapter syncAdapter;

    @Override
    public void publish(final StudentDomainEvent event) {
        // Publish to both async and sync channels
        this.asyncAdapter.publish(event);
        this.syncAdapter.publish(event);
    }
}