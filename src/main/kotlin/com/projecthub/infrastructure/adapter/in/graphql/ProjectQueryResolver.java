package com.projecthub.adapter.in.graphql;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.projecthub.adapter.in.graphql.dto.GraphQLProjectDto;
import com.projecthub.core.application.project.port.in.ProjectQueryUseCase;
import com.projecthub.core.domain.project.model.Project;

/**
 * GraphQL query resolver for projects.
 * This resolver handles GraphQL queries for retrieving project data.
 */
@Component
public class ProjectQueryResolver implements GraphQLQueryResolver {
    private final ProjectQueryUseCase projectQueryUseCase;

    public ProjectQueryResolver(ProjectQueryUseCase projectQueryUseCase) {
        this.projectQueryUseCase = projectQueryUseCase;
    }

    public List<GraphQLProjectDto> getProjects(String filter) {
        return projectQueryUseCase.searchProjects(filter).stream()
            .map(this::mapToGraphQLDto)
            .collect(Collectors.toList());
    }

    private GraphQLProjectDto mapToGraphQLDto(Project project) {
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
    }
}