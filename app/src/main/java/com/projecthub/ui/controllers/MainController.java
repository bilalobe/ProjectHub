package com.projecthub.ui.controllers;

import com.projecthub.dto.ProjectSummary;
import com.projecthub.ui.viewmodels.ProjectHubViewModel;
import com.projecthub.utils.ui.TreeItemWrapper;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MainController<projectDetails> {

    @Autowired
    private ProjectHubViewModel viewModel;

    @FXML
    private TreeView<TreeItemWrapper> schoolTreeView;

    @FXML
    private TextField searchField;

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
        bindProperties();
        setupTreeView();
        setupComponentsTable();
        bindDetailVisibility();
    }

    private void bindProperties() {
        // Bind searchField to ViewModel's searchQueryProperty
        searchField.textProperty().bindBidirectional(viewModel.searchQueryProperty());
    }

    private void setupTreeView() {
        schoolTreeView.setRoot(new TreeItem<>(new TreeItemWrapper("Schools", null)));
        schoolTreeView.setShowRoot(true);

        // Populate TreeView from ViewModel
        for (TreeItemWrapper wrapper : viewModel.getTreeItems()) {
            TreeItem<TreeItemWrapper> treeItem = new TreeItem<>(wrapper);
            schoolTreeView.getRoot().getChildren().add(treeItem);
        }

        // Customize TreeCell to display names
        schoolTreeView.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(TreeItemWrapper item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getName() == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        // Handle selection changes
        schoolTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            onItemSelected(newValue);
        });
    }

    private void onItemSelected(TreeItem<TreeItemWrapper> selectedItem) {
        if (selectedItem == null || selectedItem.getValue() == null) {
            projectDetails.setVisible(false);
            clearProjectDetails();
            return;
        }

        Object associatedObject = selectedItem.getValue().getData();

        if (associatedObject instanceof ProjectSummary projectSummary) {
            displayProjectDetails(projectSummary);
        } else {
            projectDetails.setVisible(false);
            clearProjectDetails();
        }
    }

    private void displayProjectDetails(ProjectSummary project) {
        projectNameField.setText(project.getName());
        descriptionArea.setText(project.getDescription());

        String teamName = project.getTeamId() != null ? viewModel.getTeamNameById(project.getTeamId()) : "N/A";
        teamField.setText(teamName);

        deadlineField.setText(project.getDeadline());

        loadComponents(project.getId());

        projectDetails.setVisible(true);
    }

    private void loadComponents(Long projectId) {
        List<com.projecthub.model.Component> components = viewModel.getComponentService().getComponentsByProjectId(projectId);
        List<ComponentSummary> componentSummaries = components.stream()
                .map(component -> new ComponentSummary(component.getName(), component.getDescription()))
                .collect(Collectors.toList());
        componentList.setAll(componentSummaries);
        componentsTable.setItems(componentList);
    }

    private void setupComponentsTable() {
        componentList = FXCollections.observableArrayList();
        componentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        componentDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        componentsTable.setItems(componentList);
    }

    private void bindDetailVisibility() {
        // Bind visibility of projectDetails based on selection
        projectDetails.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> schoolTreeView.getSelectionModel().getSelectedItem() != null
                        && schoolTreeView.getSelectionModel().getSelectedItem().getValue().getData() instanceof ProjectSummary,
                schoolTreeView.getSelectionModel().selectedItemProperty()
        ));
    }

    private void clearProjectDetails() {
        projectNameField.clear();
        descriptionArea.clear();
        teamField.clear();
        deadlineField.clear();
        componentList.clear();
    }

    // DTO for component summary
    public static class ComponentSummary {
        private String name;
        private String description;

        public ComponentSummary(String name, String description) {
            this.name = name;
            this.description = description;
        }

        // Getters and setters

        public String getName() {
            return name;
        }

        public void setName(String name) {
           this.name = name;
        }

        public String getDescription() {
           return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}