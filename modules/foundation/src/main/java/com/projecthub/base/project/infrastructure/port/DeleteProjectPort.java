package com.projecthub.base.project.infrastructure.port;

import java.util.UUID;

public interface DeleteProjectPort {
    void deleteProject(UUID projectId);

    void archiveProject(UUID projectId);
}
