package com.projecthub.ui.modules.cohort;

import com.projecthub.base.cohort.api.dto.CohortDTO;
import com.projecthub.base.models.School;
import com.projecthub.base.school.cohort.application.service.CohortService;
import com.projecthub.base.team.api.dto.TeamDTO;
import com.projecthub.base.team.application.service.TeamService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * ViewModel for managing cohort details.
 */
@Component
public class CohortDetailsViewModel {

    private static final Logger logger = LoggerFactory.getLogger(CohortDetailsViewModel.class);

    private final CohortService cohortService;
    private final TeamService teamService;

    private final ObjectProperty<UUID> cohortId = new SimpleObjectProperty<>();
    private final StringProperty cohortName = new SimpleStringProperty();
    private final ObjectProperty<School> school = new SimpleObjectProperty<>();
    private final ObjectProperty<UUID> schoolId = new SimpleObjectProperty<>();

    private final ObservableList<TeamDTO> teams = FXCollections.observableArrayList();
    private final ObservableList<String> schools = FXCollections.observableArrayList();
    private final StringProperty selectedSchool = new SimpleStringProperty();

    private CohortDTO cohort;

    /**
     * Constructor with dependency injection.
     *
     * @param cohortService the cohort service
     * @param teamService   the team service
     */
    public CohortDetailsViewModel(CohortService cohortService, TeamService teamService) {
        this.cohortService = cohortService;
        this.teamService = teamService;
    }

    public ObjectProperty<UUID> cohortIdProperty() {
        return cohortId;
    }

    public StringProperty cohortNameProperty() {
        return cohortName;
    }

    public ObjectProperty<School> schoolProperty() {
        return school;
    }

    public ObjectProperty<UUID> schoolIdProperty() {
        return schoolId;
    }

    public ObservableList<TeamDTO> getTeams() {
        return teams;
    }

    public ObservableList<String> getSchools() {
        return schools;
    }

    public StringProperty selectedSchoolProperty() {
        return selectedSchool;
    }

    public ObservableValue<ObservableList<String>> schoolsProperty() {
        return new SimpleObjectProperty<>(schools);
    }

    /**
     * Sets the current cohort and loads related teams.
     *
     * @param cohort the cohort DTO
     */
    public void setCohort(CohortDTO cohort) {
        if (cohort == null) {
            logger.warn("Cohort is null in setCohort()");
            clearCohort();
            return;
        }

        cohortId.set(cohort.getId());
        cohortName.set(cohort.getName());
        school.set(cohortService.getSchoolById(cohort.getSchoolId()));
        schoolId.set(cohort.getSchoolId());
        loadTeams();
    }

    private void loadTeams() {
        teams.clear();
        try {
            teams.addAll(teamService.getTeamsByCohortId(cohort.getId()));
        } catch (Exception e) {
            logger.error("Failed to load teams for cohort ID: {}", cohortId.get(), e);
        }
    }

    private void loadSchools() {
        schools.clear();
        try {
            cohortService.getAllSchools()
                .stream()
                .map(school -> school.getName())
                .forEach(schools::add);
        } catch (Exception e) {
            logger.error("Failed to load schools", e);
        }
    }

    /**
     * Saves the cohort.
     *
     * @param cohortSummary the cohort DTO
     */
    public void saveCohort(CohortDTO cohortSummary) {
        if (cohortSummary == null) {
            logger.warn("CohortSummary is null in saveCohort()");
            return;
        }
        try {
            cohortService.saveCohort(cohortSummary);
            setCohort(cohortSummary);
        } catch (Exception e) {
            logger.error("Failed to save cohort: {}", cohortSummary, e);
        }
    }

    /**
     * Deletes the cohort by ID.
     *
     * @param cohortId the cohort ID
     */
    public void deleteCohort(UUID cohortId) {
        if (cohortId == null) {
            logger.warn("CohortId is null in deleteCohort()");
            return;
        }
        try {
            cohortService.deleteCohort(cohortId);
            clearCohort();
        } catch (Exception e) {
            logger.error("Failed to delete cohort ID: {}", cohortId, e);
        }
    }

    /**
     * Clears the current cohort data.
     */
    public void clearCohort() {
        cohortId.set(null);
        cohortName.set("");
        school.set(null);
        schoolId.set(null);
        teams.clear();
    }
}
