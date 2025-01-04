package com.projecthub.ui.modules.team;

import com.projecthub.base.project.api.dto.ProjectDTO;
import com.projecthub.base.team.api.dto.TeamDTO;
import com.projecthub.base.team.application.service.TeamService;
import com.projecthub.base.users.api.dto.AppUserDTO;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * ViewModel for managing team-related data and operations.
 */
@Component
public class TeamManagementViewModel {

    private final TeamService teamService;

    // Properties
    private final ObjectProperty<TeamDTO> selectedTeam = new SimpleObjectProperty<>();
    private final ObservableList<TeamDTO> teams = FXCollections.observableArrayList();
    private final ObservableList<AppUserDTO> members = FXCollections.observableArrayList();
    private final ObservableList<ProjectDTO> projects = FXCollections.observableArrayList();

    // Read-only properties for disabling buttons
    private final BooleanProperty noTeamSelected = new SimpleBooleanProperty(true);

    public TeamManagementViewModel(TeamService teamService) {
        this.teamService = teamService;

        // Listen for changes in selectedTeam to update member and project lists
        selectedTeam.addListener((obs, oldTeam, newTeam) -> {
            if (newTeam != null) {
                loadMembers(newTeam.getId());
                loadProjects(newTeam.getId());
                noTeamSelected.set(false);
            } else {
                members.clear();
                projects.clear();
                noTeamSelected.set(true);
            }
        });

        // Initialize teams list
        loadTeams();
    }

    /**
     * Loads all teams from the service.
     */
    public void loadTeams() {
        List<TeamDTO> teamList = teamService.getAllTeams();
        teams.setAll(teamList);
    }

    /**
     * Loads team members based on team ID.
     *
     * @param teamId the ID of the team
     */
    private void loadMembers(UUID teamId) {
        List<AppUserDTO> teamMembers = teamService.getTeamMembers(teamId);
        members.setAll(teamMembers);
    }

    /**
     * Loads team projects based on team ID.
     *
     * @param teamId the ID of the team
     */
    private void loadProjects(UUID teamId) {
        List<ProjectDTO> teamProjects = teamService.getTeamProjects(teamId);
        projects.setAll(teamProjects);
    }

    // Getters and Setters

    public ObjectProperty<TeamDTO> selectedTeamProperty() {
        return selectedTeam;
    }

    public TeamDTO getSelectedTeam() {
        return selectedTeam.get();
    }

    public void setSelectedTeam(TeamDTO team) {
        this.selectedTeam.set(team);
    }

    public ObservableList<TeamDTO> getTeams() {
        return teams;
    }

    public ObservableList<AppUserDTO> getMembers() {
        return members;
    }

    public ObservableList<ProjectDTO> getProjects() {
        return projects;
    }

    public BooleanProperty noTeamSelectedProperty() {
        return noTeamSelected;
    }

    public boolean isNoTeamSelected() {
        return noTeamSelected.get();
    }

    /**
     * Adds a new team.
     *
     * @param team the TeamDTO to add
     */
    public void addTeam(TeamDTO team) {
        teamService.createTeam(team);
        teams.add(team);
    }

    /**
     * Updates an existing team.
     *
     * @param team the TeamDTO with updated information
     */
    public void updateTeam(TeamDTO team) {
        teamService.updateTeam(team.getId(), team);
        int index = teams.indexOf(team);
        if (index >= 0) {
            teams.set(index, team);
        }
    }

    /**
     * Deletes a team by ID.
     *
     * @param teamId the ID of the team to delete
     */
    public void deleteTeam(UUID teamId) {
        teamService.deleteTeam(teamId);
        teams.removeIf(team -> team.getId().equals(teamId));
        selectedTeam.set(null);
    }

    /**
     * Adds a member to the selected team.
     *
     * @param member the AppUserDTO to add
     */
    public void addMember(AppUserDTO member) {
        teamService.addMemberToTeam(selectedTeam.get().getId(), member.getId());
        members.add(member);
    }

    /**
     * Removes a member from the selected team.
     *
     * @param memberId the ID of the member to remove
     */
    public void removeMember(UUID memberId) {
        teamService.removeMemberFromTeam(selectedTeam.get().getId(), memberId);
        members.removeIf(member -> member.getId().equals(memberId));
    }

    /**
     * Adds a project to the selected team.
     *
     * @param project the ProjectDTO to add
     */
    public void addProject(ProjectDTO project) {
        teamService.addProjectToTeam(selectedTeam.get().getId(), project.getId());
        projects.add(project);
    }

    /**
     * Removes a project from the selected team.
     *
     * @param projectId the ID of the project to remove
     */
    public void removeProject(UUID projectId) {
        teamService.removeProjectFromTeam(selectedTeam.get().getId(), projectId);
        projects.removeIf(project -> project.getId().equals(projectId));
    }
}
