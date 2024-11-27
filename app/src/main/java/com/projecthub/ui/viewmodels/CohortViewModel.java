package com.projecthub.ui.viewmodels;

import com.projecthub.dto.CohortSummary;
import com.projecthub.dto.TeamSummary;
import com.projecthub.model.School;
import com.projecthub.service.CohortService;
import com.projecthub.service.TeamService;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;


@Component
public class CohortViewModel {

    private final CohortService cohortService;
    private final TeamService teamService;

    private final SimpleLongProperty cohortId = new SimpleLongProperty();
    private final SimpleStringProperty cohortName = new SimpleStringProperty();
    private final SimpleObjectProperty<School> school = new SimpleObjectProperty<>();
    private final ObservableList<TeamSummary> teams = FXCollections.observableArrayList();

    private CohortSummary cohort;

    public CohortViewModel(CohortService cohortService, TeamService teamService) {
        this.cohortService = cohortService;
        this.teamService = teamService;
    }

    public SimpleLongProperty cohortIdProperty() {
        return cohortId;
    }

    public SimpleStringProperty cohortNameProperty() {
        return cohortName;
    }

    public SimpleObjectProperty<School> schoolProperty() {
        return school;
    }

    public ObservableList<TeamSummary> getTeams() {
        return teams;
    }

    public void setCohort(CohortSummary cohort) {
        this.cohort = cohort;
        if (cohort != null) {
            cohortId.set(cohort.getId());
            cohortName.set(cohort.getName());
            school.set(cohort.getSchool());
            loadTeams();
        }
    }

    private void loadTeams() {
        teams.clear();
        teams.addAll(teamService.getTeamsByCohortId(cohort.getId()));
    }

    public void saveCohort(CohortSummary cohortSummary) {
        cohortService.saveCohort(cohortSummary);
        setCohort(cohortSummary);
    }

    public void deleteCohort(Long cohortId) {
        cohortService.deleteCohort(cohortId);
        clearCohort();
    }

    public void clearCohort() {
        cohortId.set(0);
        cohortName.set("");
        school.set(null);
        teams.clear();
    }
}