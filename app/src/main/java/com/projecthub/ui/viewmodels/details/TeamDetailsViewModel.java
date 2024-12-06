package com.projecthub.ui.viewmodels.details;

import com.projecthub.dto.AppUserDTO;
import com.projecthub.dto.ProjectDTO;
import com.projecthub.dto.TeamDTO;
import com.projecthub.service.CohortService;
import com.projecthub.service.TeamService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * ViewModel for managing team details.
 */
@Component
public class TeamDetailsViewModel {

    private static final Logger logger = LoggerFactory.getLogger(TeamDetailsViewModel.class);

    private final SimpleObjectProperty<UUID> teamId = new SimpleObjectProperty<>();
    private final SimpleStringProperty teamName = new SimpleStringProperty();
    private final SimpleStringProperty cohortName = new SimpleStringProperty();
    private final SimpleObjectProperty<UUID> schoolId = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<UUID> cohortId = new SimpleObjectProperty<>();

    private final ObservableList<ProjectDTO> projects = FXCollections.observableArrayList();
    private final ObservableList<AppUserDTO> members = FXCollections.observableArrayList();

    private final TeamService teamService;
    private final CohortService cohortService;

    private TeamDTO team;

    /**
     * Constructor with dependency injection.
     *
     * @param teamService   the team service
     * @param cohortService the cohort service
     */
    public TeamDetailsViewModel(TeamService teamService, CohortService cohortService) {
        this.teamService = teamService;
        this.cohortService = cohortService;
    }

    public SimpleObjectProperty<UUID> teamIdProperty() {
        return teamId;
    }

    public SimpleStringProperty teamNameProperty() {
        return teamName;
    }

    public SimpleStringProperty cohortNameProperty() {
        return cohortName;
    }

    public SimpleObjectProperty<UUID> schoolIdProperty() {
        return schoolId;
    }

    public SimpleObjectProperty<UUID> cohortIdProperty() {
        return cohortId;
    }

    public ObservableList<ProjectDTO> getProjects() {
        return projects;
    }

    public ObservableList<AppUserDTO> getMembers() {
        return members;
    }

    /**
     * Sets the current team and loads related data.
     *
     * @param team the team DTO
     */
    public void setTeam(TeamDTO team) {
        if (team == null) {
            logger.warn("Team is null in setTeam()");
            clearTeam();
            return;
        }

        teamId.set(team.getId());
        teamName.set(team.getName());
        schoolId.set(team.getSchoolId());
        cohortId.set(team.getCohortId());
        loadCohortName(team.getCohortId());
        loadProjects();
        loadMembers();
    }

    private void loadCohortName(UUID cohortId) {
        try {
            String name = cohortService.getCohortById(cohortId).getName();
            cohortName.set(name);
        } catch (Exception e) {
            logger.error("Failed to load cohort name for cohort ID: {}", cohortId, e);
            cohortName.set("Unknown Cohort");
        }
    }

    private void loadProjects() {
        projects.clear();
        try {
            projects.addAll(teamService.getProjectsByTeamId(team.getId()));
        } catch (Exception e) {
            logger.error("Failed to load projects for team ID: {}", teamId.get(), e);
        }
    }

    private void loadMembers() {
        members.clear();
        try {
            members.addAll(teamService.getMembersByTeamId(team.getId()));
        } catch (Exception e) {
            logger.error("Failed to load members for team ID: {}", teamId.get(), e);
        }
    }

    /**
     * Saves the team information.
     *
     * @param teamSummary the team DTO
     */
    public void saveTeam(TeamDTO teamSummary) {
        if (teamSummary == null) {
            logger.warn("TeamSummary is null in saveTeam()");
            return;
        }
        try {
            if (teamSummary.getId() != null) {
                teamService.updateTeam(teamSummary.getId(), teamSummary);
            } else {
                teamService.createTeam(teamSummary);
            }
            setTeam(teamSummary);
        } catch (Exception e) {
            logger.error("Failed to save team: {}", teamSummary, e);
        }
    }

    /**
     * Deletes the team by ID.
     *
     * @param teamId the team ID
     */
    public void deleteTeam(UUID teamId) {
        if (teamId == null) {
            logger.warn("TeamId is null in deleteTeam()");
            return;
        }
        try {
            teamService.deleteTeam(teamId);
            clearTeam();
        } catch (Exception e) {
            logger.error("Failed to delete team ID: {}", teamId, e);
        }
    }

    /**
     * Clears the current team data.
     */
    public void clearTeam() {
        teamId.set(null);
        teamName.set("");
        cohortName.set("");
        schoolId.set(null);
        cohortId.set(null);
        projects.clear();
        members.clear();
    }
}