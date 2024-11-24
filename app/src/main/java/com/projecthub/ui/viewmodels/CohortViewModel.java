package com.projecthub.ui.viewmodels;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.projecthub.dto.CohortSummary;
import com.projecthub.dto.TeamSummary;
import com.projecthub.model.Cohort;
import com.projecthub.model.Team;
import com.projecthub.service.TeamService;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Component
public class CohortViewModel {

    private final SimpleLongProperty cohortId = new SimpleLongProperty();
    private final SimpleStringProperty cohortName = new SimpleStringProperty();
    private final SimpleStringProperty schoolName = new SimpleStringProperty();

    private final ObservableList<TeamSummary> teams = FXCollections.observableArrayList();

    @Autowired
    private TeamService teamService;

    private CohortSummary cohort;

    public void setCohort(CohortSummary cohort) {
        this.cohort = cohort;
        if (cohort != null) {
            cohortId.set(cohort.getId());
            cohortName.set(cohort.getName());
            schoolName.set(cohort.getSchool().getName());
            loadTeams();
        }
    }

    private void loadTeams() {
        teams.clear();
        teams.addAll(teamService.getTeamsByCohortId(cohort.getId()));
    }

    public SimpleLongProperty cohortIdProperty() {
        return cohortId;
    }

    public SimpleStringProperty cohortNameProperty() {
        return cohortName;
    }

    public SimpleStringProperty schoolNameProperty() {
        return schoolName;
    }

    public ObservableList<TeamSummary> getTeams() {
        return teams;
    }

    public void addTeam(TeamSummary teamSummary) {
        Team team = new Team();
        team.setId(teamSummary.getId());
        Cohort cohortEntity = new Cohort();
        cohortEntity.setId(cohort.getId());
        cohortEntity.setName(cohort.getName());
        cohortEntity.setSchool(cohort.getSchool());
        team.setCohort(cohortEntity);
        teamService.saveTeam(team);
        teams.add(teamSummary);
    }
}