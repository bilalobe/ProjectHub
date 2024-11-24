package com.projecthub.ui.viewmodels;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.projecthub.dto.AppUserSummary;
import com.projecthub.dto.ProjectSummary;
import com.projecthub.dto.TeamSummary;
import com.projecthub.service.ProjectService;
import com.projecthub.service.UserService;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * ViewModel for managing team-related data and operations.
 */
@Component
public class TeamViewModel {

    private final SimpleLongProperty teamId = new SimpleLongProperty();
    private final SimpleStringProperty teamName = new SimpleStringProperty();
    private final SimpleStringProperty cohortName = new SimpleStringProperty();

    private final ObservableList<ProjectSummary> projects = FXCollections.observableArrayList();
    private final ObservableList<AppUserSummary> members = FXCollections.observableArrayList();

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    private TeamSummary team;

    /**
     * Sets the team and loads its projects and members.
     *
     * @param team the TeamSummary object representing the team
     */
    public void setTeam(TeamSummary team) {
        this.team = team;
        if (team != null) {
            teamId.set(team.getId());
            teamName.set(team.getName());
            cohortName.set(team.getCohortName());
            loadProjects();
            loadMembers();
        }
    }

    /**
     * Loads the projects associated with the team.
     */
    private void loadProjects() {
        projects.clear();
        projects.addAll(projectService.getProjectsByTeamId(team.getId()));
    }

    /**
     * Loads the members associated with the team.
     */
    private void loadMembers() {
        members.clear();
        members.addAll(userService.getUsersByTeamId(team.getId()));
    }

    /**
     * Returns the team ID property.
     *
     * @return the team ID property
     */
    public SimpleLongProperty teamIdProperty() {
        return teamId;
    }

    /**
     * Returns the team name property.
     *
     * @return the team name property
     */
    public SimpleStringProperty teamNameProperty() {
        return teamName;
    }

    /**
     * Returns the cohort name property.
     *
     * @return the cohort name property
     */
    public SimpleStringProperty cohortNameProperty() {
        return cohortName;
    }

    /**
     * Returns the list of projects associated with the team.
     *
     * @return the list of projects
     */
    public ObservableList<ProjectSummary> getProjects() {
        return projects;
    }

    /**
     * Returns the list of members associated with the team.
     *
     * @return the list of members
     */
    public ObservableList<AppUserSummary> getMembers() {
        return members;
    }

    /**
     * Adds a new project to the team.
     *
     * @param projectSummary the ProjectSummary object representing the new project
     */
    public void addProject(ProjectSummary projectSummary) {
        ProjectSummary projectWithTeamId = new ProjectSummary(
            projectSummary.getId(),
            projectSummary.getName(),
            projectSummary.getDescription(),
            team.getId(),
            projectSummary.getDeadline()
        );
        ProjectSummary savedProject = projectService.saveProject(projectWithTeamId);
        projects.add(savedProject);
    }

    /**
     * Adds a new member to the team.
     *
     * @param userSummary the AppUserSummary object representing the new member
     * @param password the password for the new member
     */
    public void addMember(AppUserSummary userSummary, String password) {
        AppUserSummary userWithTeamId = new AppUserSummary(
            userSummary.getId(),
            userSummary.getUsername(),
            password,
            team.getId()
        );
        AppUserSummary savedUser = userService.saveUser(userWithTeamId, password);
        members.add(savedUser);
    }
}