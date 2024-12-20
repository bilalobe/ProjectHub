package com.projecthub.ui.controllers.details;

import com.projecthub.core.dto.ComponentDTO;
import com.projecthub.ui.controllers.BaseController;
import com.projecthub.ui.viewmodels.details.ComponentDetailsViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ComponentDetailsController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ComponentDetailsController.class);

    private final ComponentDetailsViewModel componentViewModel;

    @FXML
    private Label componentIdLabel;

    @FXML
    private TextField componentNameField;

    @FXML
    private TextArea componentDescriptionArea;

    @FXML
    private TextField projectIdField;

    @FXML
    private Button saveComponentButton;

    @FXML
    private Button deleteComponentButton;


    public ComponentDetailsController(ComponentDetailsViewModel componentViewModel) {
        this.componentViewModel = componentViewModel;
    }

    @FXML
    public void initialize() {
        componentIdLabel.textProperty().bind(componentViewModel.componentIdProperty().asString());
        componentNameField.textProperty().bindBidirectional(componentViewModel.componentNameProperty());
        componentDescriptionArea.textProperty().bindBidirectional(componentViewModel.componentDescriptionProperty());
        projectIdField.textProperty().bind(componentViewModel.projectIdProperty().asString());

        saveComponentButton.setOnAction(this::saveComponent);
        deleteComponentButton.setOnAction(this::deleteComponent);
    }

    private void saveComponent(ActionEvent event) {
        try {
            UUID componentId = componentViewModel.componentIdProperty().get();
            String componentName = componentViewModel.componentNameProperty().get();
            String componentDescription = componentViewModel.componentDescriptionProperty().get();
            UUID projectId = componentViewModel.projectIdProperty().get();

            if (componentName == null || componentName.isEmpty()) {
                showAlert("Error", "Component name cannot be empty.");
                return;
            }

            if (projectId == null) {
                showAlert("Error", "Project ID cannot be null.");
                return;
            }

            ComponentDTO componentSummary = new ComponentDTO(
                    componentId,
                    componentName,
                    componentDescription,
                    projectId
            );

            componentViewModel.saveComponent(componentSummary);
            showAlert("Success", "Component saved successfully.");
        } catch (ValidationException ve) {
            logger.warn("Validation failed for component", ve);
            showAlert("Validation Error", ve.getMessage());
        } catch (DatabaseException de) {
            logger.error("Database error while saving component", de);
            showAlert("Database Error", "Unable to save component. Please try again later.");
        } catch (Exception e) {
            logger.error("Unexpected error while saving component", e);
            showAlert("Error", "An unexpected error occurred. Please contact support.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void deleteComponent(ActionEvent event) {
        componentViewModel.deleteComponent(componentViewModel.componentIdProperty().get());
    }
}