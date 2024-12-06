package com.projecthub.ui.controllers.details;

import com.projecthub.dto.ComponentDTO;
import com.projecthub.ui.controllers.BaseController;
import com.projecthub.ui.viewmodels.details.ComponentDetailsViewModel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        } catch (Exception e) {
            logger.error("Failed to save component", e);
            showAlert("Error", "Failed to save component.");
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