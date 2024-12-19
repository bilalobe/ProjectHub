package com.projecthub.ui.viewmodels.details;

import com.projecthub.core.dto.ComponentDTO;
import com.projecthub.core.dto.ProjectDTO;
import com.projecthub.core.services.project.ComponentService;
import com.projecthub.core.services.project.ProjectService;
import com.projecthub.core.services.team.TeamService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * ViewModel for managing project details.
 */
@Component
public class ProjectDetailsViewModel {

    private static final Logger logger = LoggerFactory.getLogger(ProjectDetailsViewModel.class);

    private final ProjectService projectService;
    private final ComponentService componentService;
    private final TeamService teamService;

    private final ObjectProperty<UUID> projectId = new SimpleObjectProperty<>();
    private final StringProperty projectName = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final ObjectProperty<UUID> teamId = new SimpleObjectProperty<>();
    private final ObjectProperty<java.time.LocalDate> deadline = new SimpleObjectProperty<>();

    private final ObservableList<ComponentDTO> components = FXCollections.observableArrayList();

    /**
     * Constructor with dependency injection.
     *
     * @param projectService   the project service
     * @param componentService the component service
     * @param teamService      the team service
     */
    public ProjectDetailsViewModel(ProjectService projectService, ComponentService componentService,
                                   TeamService teamService) {
        this.projectService = projectService;
        this.componentService = componentService;
        this.teamService = teamService;
    }

    public ObjectProperty<UUID> projectIdProperty() {
        return projectId;
    }

    public StringProperty projectNameProperty() {
        return projectName;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public ObjectProperty<UUID> teamIdProperty() {
        return teamId;
    }

    public ObjectProperty<java.time.LocalDate> deadlineProperty() {
        return deadline;
    }

    public ObservableList<ComponentDTO> getComponents() {
        return components;
    }

    /**
     * Sets the current project and loads related components.
     *
     * @param project the project DTO
     */
    public void setProject(ProjectDTO project) {
        if (project == null) {
            logger.warn("Project is null in setProject()");
            clearProject();
            return;
        }

        projectId.set(project.getId());
        projectName.set(project.getName());
        description.set(project.getDescription());
        teamId.set(project.getTeamId());
        deadline.set(project.getDeadline());
        loadComponents(project.getId());
    }

    /**
     * Loads components for the current project.
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
            List<ComponentDTO> componentList = componentService.getComponentsByProjectId(projectId);
            components.addAll(componentList);
        } catch (Exception e) {
            logger.error("Failed to load components for project ID: {}", projectId, e);
        }
    }

    /**
     * Saves the project.
     *
     * @param projectSummary the project DTO
     */
    public void saveProject(ProjectDTO projectSummary) {
        if (projectSummary == null) {
            logger.warn("ProjectSummary is null in saveProject()");
            return;
        }
        try {
            ProjectDTO savedProject;
            if (projectSummary.getId() != null) {
                savedProject = projectService.updateProject(projectSummary.getId(), projectSummary);
            } else {
                savedProject = projectService.saveProject(projectSummary);
            }
            setProject(savedProject);
        } catch (Exception e) {
            logger.error("Failed to save project: {}", projectSummary, e);
        }
    }

    /**
     * Deletes the project by ID.
     *
     * @param projectId the project ID
     */
    public void deleteProject(UUID projectId) {
        if (projectId == null) {
            logger.warn("ProjectId is null in deleteProject()");
            return;
        }
        try {
            projectService.deleteProject(projectId);
            clearProject();
        } catch (Exception e) {
            logger.error("Failed to delete project ID: {}", projectId, e);
        }
    }

    /**
     * Clears the current project data.
     */
    public void clearProject() {
        projectId.set(null);
        projectName.set("");
        description.set("");
        teamId.set(null);
        deadline.set(null);
        components.clear();
    }

    /**
     * Retrieves the team name by team ID.
     *
     * @param teamId the team ID
     * @return the team name
     */
    public String getTeamNameById(UUID teamId) {
        if (teamId == null) {
            logger.warn("TeamId is null in getTeamNameById()");
            return "Unknown Team";
        }
        return teamService.getTeamNameById(teamId);
    }
}