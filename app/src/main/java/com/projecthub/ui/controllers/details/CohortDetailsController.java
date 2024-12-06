package com.projecthub.ui.controllers.details;

import com.projecthub.dto.CohortDTO;
import com.projecthub.dto.TeamDTO;
import com.projecthub.ui.controllers.BaseController;
import com.projecthub.ui.viewmodels.details.CohortDetailsViewModel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Controller for managing cohort details.
 */
@Component
public class CohortDetailsController extends BaseController {

    private final CohortDetailsViewModel cohortViewModel;

    @FXML
    private Label cohortIdLabel;

    @FXML
    private TextField cohortNameField;

    @FXML
    private TextField schoolIdField;

    @FXML
    private TableView<TeamDTO> teamsTableView;

    @FXML
    private TableColumn<TeamDTO, UUID> teamIdColumn;

    @FXML
    private TableColumn<TeamDTO, String> teamNameColumn;

    @FXML
    private Button saveCohortButton;

    @FXML
    private Button deleteCohortButton;

    /**
     * Constructor with dependencies injected.
     *
     * @param cohortViewModel the CohortDetailsViewModel instance
     */
    public CohortDetailsController(CohortDetailsViewModel cohortViewModel) {
        this.cohortViewModel = cohortViewModel;
    }

    @FXML
    public void initialize() {
        cohortIdLabel.textProperty().bind(cohortViewModel.cohortIdProperty().asString());
        cohortNameField.textProperty().bindBidirectional(cohortViewModel.cohortNameProperty());
        schoolIdField.textProperty().bind(cohortViewModel.schoolIdProperty().asString());

        teamIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        teamsTableView.setItems(cohortViewModel.getTeams());

        saveCohortButton.setOnAction(this::saveCohort);
        deleteCohortButton.setOnAction(this::deleteCohort);
    }

    private void saveCohort(ActionEvent event) {
        try {
            CohortDTO cohortSummary = new CohortDTO(
                    cohortViewModel.cohortIdProperty().get(),
                    cohortViewModel.cohortNameProperty().get(),
                    cohortViewModel.schoolIdProperty().get());
            cohortViewModel.saveCohort(cohortSummary);
        } catch (Exception e) {
            logger.error("Failed to save cohort", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save cohort.");
        }
    }

    private void deleteCohort(ActionEvent event) {
        cohortViewModel.deleteCohort(cohortViewModel.cohortIdProperty().get());
    }
}