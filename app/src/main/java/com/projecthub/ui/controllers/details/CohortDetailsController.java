package com.projecthub.ui.controllers.details;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.projecthub.dto.CohortSummary;
import com.projecthub.dto.TeamSummary;
import com.projecthub.ui.viewmodels.CohortViewModel;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

@Component
public class CohortDetailsController {

    @Autowired
    private CohortViewModel cohortViewModel;

    @FXML
    private Label cohortNameLabel;

    @FXML
    private Label schoolNameLabel;

    @FXML
    private TableView<TeamSummary> teamTableView;

    @FXML
    private TableColumn<TeamSummary, Long> teamIdColumn;

    @FXML
    private TableColumn<TeamSummary, String> teamNameColumn;

    @FXML
    private Button addTeamButton;

    @FXML
    public void initialize() {
        cohortNameLabel.textProperty().bind(cohortViewModel.cohortNameProperty());
        schoolNameLabel.textProperty().bind(cohortViewModel.schoolNameProperty());

        teamIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        teamTableView.setItems(cohortViewModel.getTeams());

        addTeamButton.setOnAction(event -> handleAddTeam());
    }

    public void setCohort(CohortSummary cohort) {
        cohortViewModel.setCohort(cohort);
    }

    private void handleAddTeam() {
    }
}