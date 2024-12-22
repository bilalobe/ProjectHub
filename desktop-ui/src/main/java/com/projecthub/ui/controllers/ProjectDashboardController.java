package com.projecthub.ui.controllers;

import com.projecthub.core.dto.ProjectDTO;
import com.projecthub.core.services.project.ProjectService;
import com.projecthub.core.services.project.TaskService;
import com.projecthub.ui.services.navigation.NavigationService;
import com.projecthub.ui.viewmodels.ProjectDashboardViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.util.StringConverter;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class ProjectDashboardController implements Initializable {

    @FXML
    private Button backToDashboardButton;

    @FXML
    private ComboBox<ProjectDTO> projectSelector;

    @FXML
    private Label totalTaskLabel;

    @FXML
    private Label completeTaskLabel;

    @FXML
    private Label inProgressLabel;

    @FXML
    private Label pendingLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label lastUpdatedLabel;

    private ProjectDashboardViewModel viewModel;

    private final ProjectService projectService;

    private final NavigationService navigationService;
    private final TaskService taskService;

    public ProjectDashboardController(ProjectService projectService, TaskService taskService, NavigationService navigationService) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.navigationService = navigationService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize ViewModel with services
        viewModel = new ProjectDashboardViewModel(projectService, taskService);

        // Set converter for projectSelector ComboBox
        projectSelector.setConverter(new StringConverter<>() {
            @Override
            public String toString(ProjectDTO project) {
                return project != null ? project.getName() : "";
            }

            @Override
            public ProjectDTO fromString(String string) {
                return viewModel.getProjects().stream()
                        .filter(project -> project.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        // Bind projectSelector items and selection
        projectSelector.setItems(viewModel.getProjects());
        projectSelector.valueProperty().bindBidirectional(viewModel.selectedProjectProperty());

        // Listen for changes in selected project
        viewModel.selectedProjectProperty().addListener((obs, oldProject, newProject) -> {
            viewModel.updateDashboardData();
            updateStatusLabel();
        });

        // Bind labels to ViewModel properties
        totalTaskLabel.textProperty().bind(viewModel.totalTasksProperty().asString());
        completeTaskLabel.textProperty().bind(viewModel.completedTasksProperty().asString());
        inProgressLabel.textProperty().bind(viewModel.inProgressTasksProperty().asString());
        pendingLabel.textProperty().bind(viewModel.pendingTasksProperty().asString());
        lastUpdatedLabel.textProperty().bind(viewModel.lastUpdatedProperty());

        // Initialize back button
        backToDashboardButton.setOnAction(event -> handleBackButton());
    }

    private void updateStatusLabel() {
        ProjectDTO project = viewModel.selectedProjectProperty().get();
        if (project != null) {
            statusLabel.setText("Viewing project: " + project.getName());
        } else {
            statusLabel.setText("No project selected");
        }
    }

    private void handleBackButton() {
        // Implement navigation back to the main dashboard
        navigationService.navigateTo("MainDashboard");
    }
}