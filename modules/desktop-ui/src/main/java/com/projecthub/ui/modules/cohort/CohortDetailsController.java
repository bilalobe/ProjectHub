package com.projecthub.ui.modules.cohort;

import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.control.Icon;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.projecthub.base.cohort.api.dto.CohortDTO;
import com.projecthub.base.team.api.dto.TeamDTO;
import com.projecthub.ui.shared.utils.BaseController;
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
    private View cohortDetailsView;

    @FXML
    private Label cohortIdLabel;

    @FXML
    private TextField cohortNameField;

    @FXML
    private TextField schoolIdField;

    @FXML
    private ComboBox<String> schoolComboBox;

    @FXML
    private TableView<TeamDTO> teamsTableView;

    @FXML
    private TableColumn<TeamDTO, UUID> teamIdColumn;

    @FXML
    private TableColumn<TeamDTO, String> teamNameColumn;

    @FXML
    private TableColumn<TeamDTO, Integer> memberCountColumn;

    @FXML
    private TableColumn<TeamDTO, Integer> projectCountColumn;

    @FXML
    private TableColumn<TeamDTO, String> statusColumn;

    @FXML
    private Button saveCohortButton;

    @FXML
    private Button deleteCohortButton;

    @FXML
    private Icon backButton;

    @FXML
    private Label statusLabel;

    private FloatingActionButton addTeamButton;

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
        // Initialize table columns
        initializeTableColumns();

        // Bind properties
        bindProperties();

        // Set up event handlers
        setupEventHandlers();

        // Initialize FAB
        addTeamButton = new FloatingActionButton(MaterialDesignIcon.ADD.text,
            event -> showAddTeamDialog());
        addTeamButton.showOn(cohortDetailsView);
    }

    private void initializeTableColumns() {
        teamIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        memberCountColumn.setCellValueFactory(new PropertyValueFactory<>("memberCount"));
        projectCountColumn.setCellValueFactory(new PropertyValueFactory<>("projectCount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void bindProperties() {
        cohortIdLabel.textProperty().bind(cohortViewModel.cohortIdProperty().asString());
        cohortNameField.textProperty().bindBidirectional(cohortViewModel.cohortNameProperty());
        schoolIdField.textProperty().bind(cohortViewModel.schoolIdProperty().asString());
        schoolComboBox.itemsProperty().bind(cohortViewModel.schoolsProperty());
        schoolComboBox.valueProperty().bindBidirectional(cohortViewModel.selectedSchoolProperty());
        teamsTableView.setItems(cohortViewModel.getTeams());
    }

    private void setupEventHandlers() {
        saveCohortButton.setOnAction(this::saveCohort);
        deleteCohortButton.setOnAction(this::deleteCohort);
        backButton.setOnMouseClicked(event -> navigateBack());
    }

    private void showAddTeamDialog() {
        // Implement team creation dialog
    }

    private void navigateBack() {
        // Implement navigation logic
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
