package com.projecthub.adapter.in.graphql;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.projecthub.adapter.in.graphql.dto.GraphQLProjectDto;
import com.projecthub.core.application.project.port.in.ProjectManagementUseCase;
import com.projecthub.core.application.project.port.in.ProjectQueryUseCase;
import com.projecthub.core.domain.project.model.ProjectDescription;
import com.projecthub.core.domain.project.model.ProjectId;
import com.projecthub.core.domain.project.model.ProjectName;

/**
 * GraphQL mutation resolver for projects.
 * This resolver handles GraphQL mutations for creating and updating project data.
 */
@Component
public class ProjectMutationResolver implements GraphQLMutationResolver {
    private final ProjectManagementUseCase projectManagementUseCase;
    private final ProjectQueryUseCase projectQueryUseCase;

    public ProjectMutationResolver(ProjectManagementUseCase projectManagementUseCase, ProjectQueryUseCase projectQueryUseCase) {
        this.projectManagementUseCase = projectManagementUseCase;
        this.projectQueryUseCase = projectQueryUseCase;
    }

    public GraphQLProjectDto createProject(String name, String description) {
        ProjectId projectId = projectManagementUseCase.createProject(new ProjectName(name), new ProjectDescription(description));
        return mapToGraphQLDto(projectId);
    }

    public void updateProjectName(String projectId, String name) {
        projectManagementUseCase.updateProjectName(new ProjectId(UUID.fromString(projectId)), new ProjectName(name));
    }

    public void updateProjectDescription(String projectId, String description) {
        projectManagementUseCase.updateProjectDescription(new ProjectId(UUID.fromString(projectId)), new ProjectDescription(description));
    }

    private GraphQLProjectDto mapToGraphQLDto(ProjectId projectId) {
        return projectQueryUseCase.getProjectById(projectId)
            .map(project -> {
                GraphQLProjectDto dto = new GraphQLProjectDto();
                dto.setId(project.getId().getValue().toString());
                dto.setName(project.getName().getValue());
                dto.setDescription(project.getDescription().getValue());
                dto.setStatus(project.getStatus().name());
                dto.setCreatedAt(project.getCreatedAt());
                dto.setUpdatedAt(project.getUpdatedAt());
                if (project.getPriority() != null) {
                    dto.setPriority(project.getPriority().name());
                }
                if (project.getOwnerId() != null) {
                    dto.setOwnerId(project.getOwnerId().getValue().toString());
                }
                return dto;
            })
            .orElse(null);
    }
}