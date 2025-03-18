package com.projecthub.core.domain.event.project;

import com.projecthub.core.domain.event.DomainEvent;
import com.projecthub.core.domain.model.project.ProjectId;

public class ProjectCreatedEvent extends DomainEvent {
    private final ProjectId projectId;
    private final String name;

    public ProjectCreatedEvent(ProjectId projectId, String name) {
        super();
        this.projectId = projectId;
        this.name = name;
    }

    public ProjectId getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }
}
