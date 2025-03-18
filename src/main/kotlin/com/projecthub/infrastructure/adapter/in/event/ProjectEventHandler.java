package com.projecthub.infrastructure.adapter.in.event;

import com.projecthub.core.domain.event.project.ProjectCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ProjectEventHandler {
    private static final Logger log = LoggerFactory.getLogger(ProjectEventHandler.class);

    @EventListener
    public void handleProjectCreatedEvent(ProjectCreatedEvent event) {
        log.info("Project created: {} with name {}", event.getProjectId().getValue(), event.getName());
        // Add any additional handling here
    }
}
