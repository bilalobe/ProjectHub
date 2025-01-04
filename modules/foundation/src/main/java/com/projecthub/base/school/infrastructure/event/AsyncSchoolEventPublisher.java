package com.projecthub.base.school.infrastructure.event;

import com.projecthub.base.school.domain.event.SchoolDomainEvent;
import com.projecthub.base.school.domain.event.SchoolEventPublisher;
import com.projecthub.base.school.infrastructure.event.adapter.AsyncSchoolEventAdapter;
import com.projecthub.base.school.infrastructure.event.adapter.SyncSchoolEventAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncSchoolEventPublisher implements SchoolEventPublisher {
    private final AsyncSchoolEventAdapter asyncAdapter;
    private final SyncSchoolEventAdapter syncAdapter;

    @Override
    public void publish(SchoolDomainEvent event) {
        // Publish to both async and sync channels
        asyncAdapter.publish(event);
        syncAdapter.publish(event);
    }
}
