package com.projecthub.base.project.infrastructure.port.in;

import com.projecthub.base.project.api.dto.ProjectDTO;
import com.projecthub.base.project.domain.enums.ProjectStatus;

import java.util.UUID;

public interface ProjectManagementUseCase {
    ProjectDTO createProject(ProjectDTO project);

    void updateProjectStatus(UUID id, ProjectStatus status);
}

