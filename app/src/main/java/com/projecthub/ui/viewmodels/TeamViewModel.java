package com.projecthub.ui.viewmodels;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.projecthub.dto.AppUserSummary;
import com.projecthub.dto.ProjectSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.model.AppUser;
import com.projecthub.model.Project;
import com.projecthub.model.Team;
import com.projecthub.service.ProjectService;
import com.projecthub.service.UserService;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * ViewModel for the TeamDetails view.
 */
@Component
public class TeamViewModel {

    private final SimpleLongProperty teamId = new SimpleLongProperty();
    private final SimpleStringProperty teamName = new SimpleStringProperty();
    private final SimpleStringProperty cohortName = new SimpleStringProperty();

    private final ObservableList<Project> projects = FXCollections.observableArrayList();
    private final ObservableList<AppUser> members = FXCollections.observableArrayList();

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    private Team team;

    /**
     * Initializes the ViewModel with a Team.
     *
     * @param team the Team to display
     */
    public void setTeam(Team team) throws ResourceNotFoundException {
        this.team = team;
        if (team != null) {
            teamId.set(team.getId());
            teamName.set(team.getName());
            cohortName.set(team.getCohort().getName());
            loadProjects();
            loadMembers();
        }
    }

    /**
     * Loads projects associated with the team.
     * @throws ResourceNotFoundException if the projects cannot be loaded 
     */
    private void loadProjects() throws ResourceNotFoundException {
        projects.clear();
        List<ProjectSummary> projectSummaries = projectService.getProjectsByTeamId(team.getId());
        List<Project> projectList = projectSummaries.stream()
                .map(summary -> new Project(summary.getId(), summary.getName(), null, team))
                .collect(Collectors.toList());
        projects.addAll(projectList);
    }

    /**
     * Loads members associated with the team.
     */
    private void loadMembers() {
        members.clear();
        members.addAll(userService.getUsersByTeamId(team.getId()));
    }

    // Getters for property bindings

    public SimpleLongProperty teamIdProperty() {
        return teamId;
    }

    public SimpleStringProperty teamNameProperty() {
        return teamName;
    }

    public SimpleStringProperty cohortNameProperty() {
        return cohortName;
    }

    public ObservableList<Project> getProjects() {
        return projects;
    }

    public ObservableList<AppUser> getMembers() {
        return members;
    }

    // Methods to add new projects and members

    /**
     * Adds a new project to the team.
     *
     * @param project the Project to add
     */
    public void addProject(Project project) {
        project.setTeam(team);
        projectService.saveProject(project);
        projects.add(project);
    }

    /**
     * Adds a new member to the team.
     *
     * @param user the AppUser to add
     */
    public void addMember(AppUserSummary user) {
        user.setTeam(team);
        userService.saveUser(user);
        members.add(user);
    }
}