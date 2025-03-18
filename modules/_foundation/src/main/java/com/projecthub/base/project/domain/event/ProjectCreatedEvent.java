package com.projecthub.base.project.domain.event;

import com.projecthub.base.project.domain.entity.Project;

import java.time.Instant;
import java.util.UUID;

public record ProjectCreatedEvent(
    UUID projectId,
    String projectName,
    UUID teamId,
    UUID initiatorId
) implements ProjectEvent {

    public ProjectCreatedEvent(final Project project, final UUID initiatorId) {
        this(
            project.getId(),
            project.getName(),
            project.getTeam().teamId(),
            initiatorId
        );
    }

    @Override
    public UUID getProjectId() {
        return this.projectId;
    }

    @Override
    public String getProjectName() {
        return this.projectName;
    }
}
