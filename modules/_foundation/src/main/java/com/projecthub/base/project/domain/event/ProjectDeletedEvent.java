package com.projecthub.base.project.domain.event;

import java.util.UUID;

public record ProjectDeletedEvent(
    UUID projectId,
    String projectName,
    UUID initiatorId
) implements ProjectEvent {
    @Override
    public UUID getProjectId() {
        return projectId();
    }

    @Override
    public String getProjectName() {
        return projectName();
    }
}
