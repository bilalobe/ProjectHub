package com.projecthub.ui.viewmodels;

import com.projecthub.dto.AppUserSummary;
import com.projecthub.dto.ProjectSummary;
import com.projecthub.dto.TeamSummary;
import com.projecthub.mapper.TeamMapper;
import com.projecthub.model.Cohort;
import com.projecthub.model.School;
import com.projecthub.model.Team;
import com.projecthub.service.TeamService;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;

@Component
public class TeamViewModel {

    private final SimpleLongProperty teamId = new SimpleLongProperty();
    private final SimpleStringProperty teamName = new SimpleStringProperty();
    private final SimpleStringProperty cohortName = new SimpleStringProperty();

    private final ObservableList<ProjectSummary> projects = FXCollections.observableArrayList();
    private final ObservableList<AppUserSummary> members = FXCollections.observableArrayList();

    private final TeamService teamService;
    private final TeamMapper teamMapper;

    private TeamSummary team;

    public TeamViewModel(TeamService teamService, TeamMapper teamMapper) {
        this.teamService = teamService;
        this.teamMapper = teamMapper;
    }

    public SimpleLongProperty teamIdProperty() {
        return teamId;
    }

    public SimpleStringProperty teamNameProperty() {
        return teamName;
    }

    public SimpleStringProperty cohortNameProperty() {
        return cohortName;
    }

    public ObservableList<ProjectSummary> getProjects() {
        return projects;
    }

    public ObservableList<AppUserSummary> getMembers() {
        return members;
    }

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

    private void loadProjects() {
        projects.clear();
        projects.addAll(teamService.getProjectsByTeamId(team.getId()));
    }

    private void loadMembers() {
        members.clear();
        members.addAll(teamService.getMembersByTeamId(team.getId()));
    }

    public void saveTeam(TeamSummary teamSummary) {
        School school = teamService.getSchoolById(teamSummary.getSchoolId());
        Cohort cohort = teamService.getCohortById(teamSummary.getCohortId());
        Team newTeam = teamMapper.toTeam(teamSummary, school, cohort);
        teamService.saveTeam(newTeam);
        setTeam(teamMapper.toTeamSummary(newTeam));
    }

    public void deleteTeam(Long teamId) {
        teamService.deleteTeam(teamId);
        clearTeam();
    }

    public void clearTeam() {
        teamId.set(0);
        teamName.set("");
        cohortName.set("");
        projects.clear();
        members.clear();
    }
}