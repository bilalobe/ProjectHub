package com.projecthub.base.project.infrastructure.port;

import com.projecthub.base.project.api.command.UpdateProjectCommand;
import com.projecthub.base.project.domain.enums.ProjectStatus;

import java.util.UUID;

public interface UpdateProjectPort {
    void updateProject(UpdateProjectCommand command);

    void updateStatus(UUID projectId, ProjectStatus newStatus);

    void assignTeam(UUID projectId, UUID teamId);
}
