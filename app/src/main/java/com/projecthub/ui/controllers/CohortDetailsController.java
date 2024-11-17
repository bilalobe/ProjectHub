package com.projecthub.ui.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.projecthub.model.Cohort;
import com.projecthub.model.Team;
import com.projecthub.service.TeamService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for displaying Cohort details.
 */
@Component
public class CohortDetailsController {

    @Autowired
    private TeamService teamService;

    @FXML
    private Label cohortNameLabel;

    @FXML
    private Label schoolNameLabel;

    @FXML
    private TableView<Team> teamTableView;

    @FXML
    private TableColumn<Team, Long> teamIdColumn;

    @FXML
    private TableColumn<Team, String> teamNameColumn;

    @FXML
    private Button addTeamButton;

    private Cohort cohort;

    /**
     * Sets the Cohort to display.
     *
     * @param cohort the Cohort
     */
    public void setCohort(Cohort cohort) {
        this.cohort = cohort;
        updateUI();
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        teamIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        teamNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
    }

    /**
     * Updates the UI elements with Cohort data.
     */
    private void updateUI() {
        if (cohort != null) {
            cohortNameLabel.setText(cohort.getName());
            schoolNameLabel.setText(cohort.getSchool().getName());
            loadTeams();
        }
    }

    /**
     * Loads the teams associated with the Cohort.
     */
    private void loadTeams() {
        List<Team> teams = teamService.getTeamsByCohortId(cohort.getId());
        ObservableList<Team> teamList = FXCollections.observableArrayList(teams);
        teamTableView.setItems(teamList);
    }

    /**
     * Handles the action of adding a new Team.
     */
    @FXML
    private void handleAddTeam() {
        // Implement adding a new team to the cohort
    }
}