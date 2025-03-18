package com.projecthub.base.project.api.graphql;

import com.projecthub.base.project.api.dto.ProjectDTO;
import com.projecthub.base.project.application.facade.ProjectFacade;
import com.projecthub.base.project.domain.enums.ProjectStatus;
import com.projecthub.base.project.infrastructure.service.ProjectSearchService.ProjectSearchCriteria;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.relay.Connection;
import graphql.relay.SimpleListConnection;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProjectGraphQLResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    private final ProjectFacade projectFacade;

    // Queries
    public ProjectDTO project(UUID id) {
        return projectFacade.getProjectById(id);
    }

    public List<ProjectDTO> projects() {
        return projectFacade.getAllProjects();
    }

    public Connection<ProjectDTO> projectConnection(int first, String after, DataFetchingEnvironment env) {
        List<ProjectDTO> allProjects = projectFacade.getAllProjects();
        return new SimpleListConnection<>(allProjects).get(env);
    }

    public Page<ProjectDTO> searchProjects(ProjectSearchInput input) {
        ProjectSearchCriteria criteria = new ProjectSearchCriteria(
            input.getName(),
            input.getStatus(),
            input.getTeamId(),
            input.getStartDateFrom(),
            input.getStartDateTo(),
            input.getDeadlineFrom(),
            input.getDeadlineTo(),
            input.getSortField(),
            input.isAscending()
        );
        return projectFacade.searchProjects(criteria, PageRequest.of(0, 20));
    }

    public ProjectStatistics projectStatistics(UUID teamId) {
        return projectFacade.getProjectStatistics(teamId);
    }

    // Mutations
    public ProjectDTO createProject(ProjectInput input) {
        ProjectDTO projectDTO = mapInputToDTO(input);
        return projectFacade.createProject(projectDTO);
    }

    public ProjectDTO updateProject(UUID id, ProjectInput input) {
        ProjectDTO projectDTO = mapInputToDTO(input);
        return projectFacade.updateProject(id, projectDTO);
    }

    public Boolean updateProjectStatus(UUID id, ProjectStatus status) {
        projectFacade.updateProjectStatus(id, status);
        return true;
    }

    public Boolean deleteProject(UUID id) {
        projectFacade.deleteProject(id);
        return true;
    }

    private ProjectDTO mapInputToDTO(ProjectInput input) {
        return new ProjectDTO(
            null,
            input.getName(),
            input.getDescription(),
            input.getTeamId(),
            input.getStartDate(),
            input.getDeadline(),
            null,
            null
        );
    }
}