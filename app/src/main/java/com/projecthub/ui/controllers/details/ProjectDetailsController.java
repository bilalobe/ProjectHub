package com.projecthub.ui.controllers.details;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.projecthub.dto.ComponentSummary;
import com.projecthub.dto.ProjectSummary;
import com.projecthub.ui.viewmodels.ProjectHubViewModel;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

@Component
public class ProjectDetailsController {

    @Autowired
    private ProjectHubViewModel viewModel;

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
    private TableView<ComponentSummary> componentsTable;
    @FXML
    private TableColumn<ComponentSummary, String> componentNameColumn;
    @FXML
    private TableColumn<ComponentSummary, String> componentDescriptionColumn;

    private ObservableList<ComponentSummary> componentList;

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
    public void displayProjectDetails(ProjectSummary project) {
        projectNameField.setText(project.getName());
        descriptionArea.setText(project.getDescription());

        String teamName = project.getTeam() != null ? viewModel.getTeamNameById(project.getTeam()) : "N/A";
        teamField.setText(teamName);

        deadlineField.setText(project.getDeadline());

        loadComponents(project.getId());

        projectDetails.setVisible(true);
    }

    /**
     * Loads the components of the selected project.
     *
     * @param projectId the ID of the project
     */
    private void loadComponents(Long projectId) {
        List<ComponentSummary> components = viewModel.getComponentService().getComponentsByProjectId(projectId);
        componentList.setAll(components);
        componentsTable.setItems(componentList);
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
        projectDetails.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> projectDetails.isVisible(),
                projectDetails.visibleProperty()
        ));
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
}