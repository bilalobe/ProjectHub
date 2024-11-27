package com.projecthub.ui.viewmodels;

import com.projecthub.dto.CohortSummary;
import com.projecthub.dto.ComponentSummary;
import com.projecthub.dto.ProjectSummary;
import com.projecthub.dto.TeamSummary;
import com.projecthub.service.CohortService;
import com.projecthub.service.ComponentService;
import com.projecthub.service.ProjectService;
import com.projecthub.service.TeamService;
import com.projecthub.utils.ui.TreeItemWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectHubViewModel {

    private final ProjectService projectService;
    private final CohortService cohortService;
    private final TeamService teamService;
    private final ComponentService componentService;

    private final ObservableList<ProjectSummary> projects = FXCollections.observableArrayList();
    private final ObservableList<ComponentSummary> components = FXCollections.observableArrayList();
    private final StringProperty searchQuery = new SimpleStringProperty();

    public ProjectHubViewModel(ProjectService projectService, CohortService cohortService, TeamService teamService, ComponentService componentService) {
        this.projectService = projectService;
        this.cohortService = cohortService;
        this.teamService = teamService;
        this.componentService = componentService;
        initialize();
    }

    public ObservableList<ProjectSummary> getProjects() {
        return projects;
    }

    public ObservableList<ComponentSummary> getComponents() {
        return components;
    }

    public StringProperty searchQueryProperty() {
        return searchQuery;
    }

    private void initialize() {
        loadProjects();
        searchQuery.addListener((observable, oldValue, newValue) -> searchProjects());
    }

    public void loadProjects() {
        List<ProjectSummary> projectSummaries = projectService.getAllProjects();
        projects.setAll(projectSummaries);
    }

    public void searchProjects() {
        String query = searchQuery.get().toLowerCase();
        List<ProjectSummary> filteredProjects = projectService.getAllProjects().stream()
                .filter(project -> project.getName().toLowerCase().contains(query) ||
                        project.getDescription().toLowerCase().contains(query))
                .collect(Collectors.toList());
        projects.setAll(filteredProjects);
    }

    public void saveProject(ProjectSummary projectSummary) {
        projectService.saveProject(projectSummary);
        loadProjects();
    }

    public void deleteProject(Long projectId) {
        projectService.deleteProject(projectId);
        loadProjects();
    }

    public TreeItemWrapper[] getTreeItems() {
        List<ProjectSummary> projectSummaries = projectService.getAllProjects();
        return projectSummaries.stream()
                .map(project -> new TreeItemWrapper(String.valueOf(project.getId()), project.getName()))
                .toArray(TreeItemWrapper[]::new);
    }

    public List<CohortSummary> getClassesBySchoolId(Long id) {
        return cohortService.getCohortsBySchoolId(id);
    }

    public String getTeamNameById(Long teamId) {
        TeamSummary team = teamService.getTeamById(teamId);
        return team != null ? team.getName() : "Unknown Team";
    }

    public void loadComponents(Long projectId) {
        List<ComponentSummary> componentSummaries = componentService.getComponentsByProjectId(projectId);
        components.setAll(componentSummaries);
    }
}