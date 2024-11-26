package com.projecthub.ui.controllers.details;

import com.projecthub.dto.CohortSummary;
import com.projecthub.dto.TeamSummary;
import com.projecthub.ui.viewmodels.CohortViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CohortDetailsController {

    @Autowired
    private CohortViewModel cohortViewModel;

    @FXML
    private Label cohortIdLabel;

    @FXML
    private Label cohortNameLabel;

    @FXML
    private Label schoolNameLabel;

    @FXML
    private TableView<TeamSummary> teamsTableView;

    @FXML
    private TableColumn<TeamSummary, Long> teamIdColumn;

    @FXML
    private TableColumn<TeamSummary, String> teamNameColumn;

    @FXML
    private Button saveCohortButton;

    @FXML
    private Button deleteCohortButton;

    @FXML
    public void initialize() {
        cohortIdLabel.textProperty().bind(cohortViewModel.cohortIdProperty().asString());
        cohortNameLabel.textProperty().bind(cohortViewModel.cohortNameProperty());
        schoolNameLabel.textProperty().bind(cohortViewModel.schoolProperty().asString());

        teamIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        teamsTableView.setItems(cohortViewModel.getTeams());

        saveCohortButton.setOnAction(event -> saveCohort());
        deleteCohortButton.setOnAction(event -> deleteCohort());
    }

    public void setCohort(CohortSummary cohort) {
        cohortViewModel.setCohort(cohort);
    }

    private void saveCohort() {
        CohortSummary cohortSummary = new CohortSummary(
                cohortViewModel.cohortIdProperty().get(),
                cohortViewModel.cohortNameProperty().get(),
                cohortViewModel.schoolProperty().get()
        );
        cohortViewModel.saveCohort(cohortSummary);
    }

    private void deleteCohort() {
        cohortViewModel.deleteCohort(cohortViewModel.cohortIdProperty().get());
    }
}