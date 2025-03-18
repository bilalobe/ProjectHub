package com.projecthub.core.domain.event.project;

import com.projecthub.core.domain.event.DomainEvent;
import com.projecthub.core.domain.model.project.ProjectId;
import com.projecthub.core.domain.model.project.ProjectStatus;

public class ProjectStatusChangedEvent extends DomainEvent {
    private final ProjectId projectId;
    private final ProjectStatus oldStatus;
    private final ProjectStatus newStatus;

    public ProjectStatusChangedEvent(ProjectId projectId, ProjectStatus oldStatus, ProjectStatus newStatus) {
        this.projectId = projectId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public ProjectId getProjectId() {
        return projectId;
    }

    public ProjectStatus getOldStatus() {
        return oldStatus;
    }

    public ProjectStatus getNewStatus() {
        return newStatus;
    }
}
