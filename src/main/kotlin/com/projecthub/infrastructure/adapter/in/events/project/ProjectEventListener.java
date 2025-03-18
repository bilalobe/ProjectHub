package com.projecthub.infrastructure.adapter.in.events.project;

import com.projecthub.core.domain.event.project.ProjectCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ProjectEventListener {

    @Async
    @EventListener
    public void handleProjectCreated(ProjectCreatedEvent event) {
        // Handle project created event
        System.out.println("Project created: " + event.getProjectId());
    }
}
