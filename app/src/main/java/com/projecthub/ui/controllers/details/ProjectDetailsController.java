package com.projecthub.ui.controllers.details;

import com.projecthub.dto.ComponentDTO;
import com.projecthub.dto.ProjectDTO;
import com.projecthub.ui.controllers.BaseController;
import com.projecthub.ui.viewmodels.details.ProjectDetailsViewModel;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Controller for managing project details.
 */
@Component
public class ProjectDetailsController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectDetailsController.class);

    private final ProjectDetailsViewModel viewModel;

    @FXML
    private GridPane projectDetails;

    @FXML
    private TextField projectNameField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField teamField;

    @FXML
    private TextField deadlineField;

    @FXML
    private TableView<ComponentDTO> componentsTable;

    @FXML
    private TableColumn<ComponentDTO, String> componentNameColumn;

    @FXML
    private TableColumn<ComponentDTO, String> componentDescriptionColumn;

    private ObservableList<ComponentDTO> componentList;

    /**
     * Constructor with dependencies injected.
     *
     * @param viewModel the ProjectDetailsViewModel instance
     */
    public ProjectDetailsController(ProjectDetailsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @FXML
    public void initialize() {
        setupComponentsTable();
        bindDetailVisibility();
    }

    /**
     * Displays the details of the selected project.
     *
     * @param project the project to display
     */
    public void displayProjectDetails(ProjectDTO project) {
        try {
            projectNameField.setText(project.getName());
            descriptionArea.setText(project.getDescription());

            String teamName = project.getTeamId() != null ? viewModel.getTeamNameById(project.getTeamId()) : "N/A";
            teamField.setText(teamName);

            deadlineField.setText(project.getDeadline() != null ? project.getDeadline().toString() : "N/A");

            loadComponents(project.getId());

            projectDetails.setVisible(true);
        } catch (Exception e) {
            logger.error("Failed to display project details", e);
            showAlert("Error", "Failed to display project details.");
        }
    }

    /**
     * Loads the components of the selected project.
     *
     * @param projectId the ID of the project
     */
    private void loadComponents(UUID projectId) {
        try {
            viewModel.loadComponents(projectId);
            componentsTable.setItems(viewModel.getComponents());
        } catch (Exception e) {
            showAlert("Error", "Failed to load components: " + e.getMessage());
        }
    }

    /**
     * Sets up the components table.
     */
    private void setupComponentsTable() {
        componentList = FXCollections.observableArrayList();
        componentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        componentDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        componentsTable.setItems(componentList);
    }

    /**
     * Binds the visibility of the project details pane.
     */
    private void bindDetailVisibility() {
        projectDetails.visibleProperty().bind(
                Bindings.createBooleanBinding(() -> projectDetails.isVisible(), projectDetails.visibleProperty()));
    }

    /**
     * Clears the project details fields.
     */
    public void clearProjectDetails() {
        projectNameField.clear();
        descriptionArea.clear();
        teamField.clear();
        deadlineField.clear();
        componentList.clear();
    }

    /**
     * Shows an alert with the specified title and message.
     *
     * @param title   the title of the alert
     * @param message the message of the alert
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}