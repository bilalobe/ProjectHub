package com.projecthub.base.project.infrastructure.port;

import com.projecthub.base.project.api.command.CreateProjectCommand;

import java.util.UUID;

public interface CreateProjectPort {
    UUID createProject(CreateProjectCommand command);

    boolean existsByName(String name);
}
