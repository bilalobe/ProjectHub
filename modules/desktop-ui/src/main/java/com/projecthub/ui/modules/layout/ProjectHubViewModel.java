package com.projecthub.ui.modules.layout;

import com.projecthub.base.cohort.api.dto.CohortDTO;
import com.projecthub.base.component.api.dto.ComponentDTO;
import com.projecthub.base.component.application.service.ComponentService;
import com.projecthub.base.project.api.dto.ProjectDTO;
import com.projecthub.base.project.mgmt.domain.service.ProjectService;
import com.projecthub.base.school.cohort.application.service.CohortService;
import com.projecthub.base.team.api.dto.TeamDTO;
import com.projecthub.base.team.application.service.TeamService;
import com.projecthub.ui.shared.utils.TreeItemWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * ViewModel for managing the Project Hub.
 */
@Component
public class ProjectHubViewModel {

    private static final Logger logger = LoggerFactory.getLogger(ProjectHubViewModel.class);

    private final ProjectService projectService;
    private final CohortService cohortService;
    private final TeamService teamService;
    private final ComponentService componentService;

    private final ObservableList<ProjectDTO> projects = FXCollections.observableArrayList();
    private final ObservableList<ComponentDTO> components = FXCollections.observableArrayList();
    private final StringProperty searchQuery = new SimpleStringProperty();

    /**
     * Constructor with dependency injection.
     *
     * @param projectService   the project service
     * @param cohortService    the cohort service
     * @param teamService      the team service
     * @param componentService the component service
     */
    public ProjectHubViewModel(ProjectService projectService, CohortService cohortService, TeamService teamService, ComponentService componentService) {
        this.projectService = projectService;
        this.cohortService = cohortService;
        this.teamService = teamService;
        this.componentService = componentService;
        initialize();
    }

    public ObservableList<ProjectDTO> getProjects() {
        return projects;
    }

    public ObservableList<ComponentDTO> getComponents() {
        return components;
    }

    public StringProperty searchQueryProperty() {
        return searchQuery;
    }

    private void initialize() {
        loadProjects();
        searchQuery.addListener(this::onSearchQueryChanged);
    }

    /**
     * Loads projects from the service.
     */
    public void loadProjects() {
        try {
            List<ProjectDTO> projectSummaries = projectService.getAllProjects();
            projects.setAll(projectSummaries);
        } catch (Exception e) {
            logger.error("Failed to load projects", e);
        }
    }

    /**
     * Searches projects based on the search query.
     *
     * @param query the search query
     */
    public void searchProjects(String query) {
        if (query == null || query.isEmpty()) {
            logger.warn("Search query is null or empty in searchProjects()");
            return;
        }
        try {
            String lowerCaseQuery = query.toLowerCase();
            List<ProjectDTO> filteredProjects = projectService.getAllProjects().stream()
                .filter(project -> project.getName().toLowerCase().contains(lowerCaseQuery) ||
                    project.getDescription().toLowerCase().contains(lowerCaseQuery))
                .toList();
            projects.setAll(filteredProjects);
        } catch (Exception e) {
            logger.error("Failed to search projects with query: {}", query, e);
        }
    }

    /**
     * Saves a project.
     *
     * @param projectSummary the project DTO
     */
    public void saveProject(ProjectDTO projectSummary) {
        if (projectSummary == null) {
            logger.warn("ProjectSummary is null in saveProject()");
            return;
        }
        executeProjectOperation(() -> projectService.saveProject(projectSummary), "save", projectSummary);
    }

    /**
     * Deletes a project by ID.
     *
     * @param projectId the project ID
     */
    public void deleteProject(UUID projectId) {
        if (projectId == null) {
            logger.warn("ProjectId is null in deleteProject()");
            return;
        }
        executeProjectOperation(() -> projectService.deleteProject(projectId), "delete", projectId);
    }

    private void executeProjectOperation(Runnable operation, String operationName, Object projectIdentifier) {
        try {
            operation.run();
            loadProjects();
        } catch (Exception e) {
            logger.error("Failed to {} project: {}", operationName, projectIdentifier, e);
        }
    }

    public TreeItemWrapper[] getTreeItems() {
        List<ProjectDTO> projectSummaries = projectService.getAllProjects();
        return projectSummaries.stream()
            .map(project -> new TreeItemWrapper(String.valueOf(project.getId()), project.getName()))
            .toArray(TreeItemWrapper[]::new);
    }

    public List<CohortDTO> getClassesBySchoolId(UUID id) {
        return cohortService.getCohortsBySchoolId(id);
    }

    public String getTeamNameById(UUID teamId) {
        TeamDTO team = teamService.getTeamById(teamId);
        return team != null ? team.getName() : "Unknown Team";
    }

    /**
     * Loads components for a given project ID.
     *
     * @param projectId the project ID
     */
    public void loadComponents(UUID projectId) {
        if (projectId == null) {
            logger.warn("ProjectId is null in loadComponents()");
            return;
        }
        components.clear();
        try {
            List<ComponentDTO> componentSummaries = componentService.getComponentsByProjectId(projectId);
            components.setAll(componentSummaries);
        } catch (Exception e) {
            logger.error("Failed to load components for project ID: {}", projectId, e);
        }
    }

    /**
     * Handles changes to the search query.
     *
     * @param observable the observable value
     * @param oldValue   the old search query
     * @param newValue   the new search query
     */
    private void onSearchQueryChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        searchProjects(newValue);
    }
}
