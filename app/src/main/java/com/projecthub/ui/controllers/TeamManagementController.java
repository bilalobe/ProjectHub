package com.projecthub.ui.controllers;

import com.projecthub.core.dto.AppUserDTO;
import com.projecthub.core.dto.ProjectDTO;
import com.projecthub.core.dto.TeamDTO;
import com.projecthub.ui.viewmodels.TeamManagementViewModel;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Controller for handling team management UI interactions.
 */
@Component
public class TeamManagementController extends BaseController {

    private final TeamManagementViewModel teamViewModel;

    @FXML
    private Button backButton;

    @FXML
    private ComboBox<TeamDTO> teamSelector;

    @FXML
    private TextField teamNameField;

    @FXML
    private ComboBox<String> cohortSelector;

    @FXML
    private Label teamNameError;

    @FXML
    private Label cohortError;

    @FXML
    private Button addMemberButton;

    @FXML
    private TableView<AppUserDTO> membersTable;

    @FXML
    private TableColumn<AppUserDTO, String> memberNameColumn;

    @FXML
    private TableColumn<AppUserDTO, String> memberRoleColumn;

    @FXML
    private TableColumn<AppUserDTO, String> memberEmailColumn;

    @FXML
    private TableColumn<AppUserDTO, Void> memberActionColumn;

    @FXML
    private Button addProjectButton;

    @FXML
    private TableView<ProjectDTO> projectsTable;

    @FXML
    private TableColumn<ProjectDTO, String> projectNameColumn;

    @FXML
    private TableColumn<ProjectDTO, String> projectStatusColumn;

    @FXML
    private TableColumn<ProjectDTO, String> projectDeadlineColumn;

    @FXML
    private TableColumn<ProjectDTO, Void> projectActionColumn;

    @FXML
    private Label statusLabel;

    @FXML
    private Label teamIdLabel;

    /**
     * Constructor with dependencies injected.
     *
     * @param teamViewModel the TeamManagementViewModel instance
     */
    public TeamManagementController(TeamManagementViewModel teamViewModel) {
        this.teamViewModel = teamViewModel;
    }

    @FXML
    public void initialize() {
        // Bind teams to teamSelector
        teamSelector.setItems(teamViewModel.getTeams());
        teamSelector.valueProperty().bindBidirectional(teamViewModel.selectedTeamProperty());

        // Bind team name and cohort selectors
        teamNameField.textProperty().bindBidirectional(teamViewModel.selectedTeamProperty().get().nameProperty());
        cohortSelector.setItems(FXCollections.observableArrayList("Cohort 1", "Cohort 2", "Cohort 3")); // Example cohorts
        cohortSelector.valueProperty().bindBidirectional(teamViewModel.selectedTeamProperty().get().cohortProperty());

        // Bind error labels visibility
        teamNameError.visibleProperty().bind(Bindings.isNotEmpty(teamViewModel.selectedTeamProperty().get().nameProperty().getPendingValue().asString()));
        cohortError.visibleProperty().bind(Bindings.isNotEmpty(cohortSelector.valueProperty()));

        // Initialize Members Table
        memberNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        memberRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        memberEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        initializeMemberActionColumn();

        membersTable.setItems(teamViewModel.getMembers());

        // Set custom row factory for membersTable
        membersTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(AppUserDTO item, boolean empty) {
                super.updateItem(item, empty);
                // Customize row if needed
            }
        });

        // Initialize Projects Table
        projectNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        projectStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        projectDeadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        initializeProjectActionColumn();

        projectsTable.setItems(teamViewModel.getProjects());

        // Set custom row factory for projectsTable if needed
        projectsTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(ProjectDTO item, boolean empty) {
                super.updateItem(item, empty);
                // Customize row if needed
            }
        });

        // Bind buttons' disable property
        addMemberButton.disableProperty().bind(teamViewModel.noTeamSelectedProperty());
        addProjectButton.disableProperty().bind(teamViewModel.noTeamSelectedProperty());

        // Bind status and team ID labels
        statusLabel.textProperty().bind(teamViewModel.noTeamSelectedProperty().not().asString().map(selected -> selected ? "Ready" : "Team Selected"));
        teamIdLabel.textProperty().bind(Bindings.selectString(teamViewModel.selectedTeamProperty(), "id").asString());

        // Set actions for buttons
        backButton.setOnAction(this::handleBack);
        addMemberButton.setOnAction(this::handleAddMember);
        addProjectButton.setOnAction(this::handleAddProject);
    }

    /**
     * Initializes the action column for members table with Edit and Remove options.
     */
    private void initializeMemberActionColumn() {
        memberActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button removeButton = new Button("Remove");
            private final HBox pane = new HBox(5, editButton, removeButton);

            {
                editButton.setOnAction(event -> {
                    AppUserDTO member = getTableView().getItems().get(getIndex());
                    handleEditMember(event, member);
                });

                removeButton.setOnAction(event -> {
                    AppUserDTO member = getTableView().getItems().get(getIndex());
                    handleRemoveMember(event, member);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
    }

    /**
     * Initializes the action column for projects table with Edit and Remove options.
     */
    private void initializeProjectActionColumn() {
        projectActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button removeButton = new Button("Remove");
            private final HBox pane = new HBox(5, editButton, removeButton);

            {
                editButton.setOnAction(event -> {
                    ProjectDTO project = getTableView().getItems().get(getIndex());
                    handleEditProject(event, project);
                });

                removeButton.setOnAction(event -> {
                    ProjectDTO project = getTableView().getItems().get(getIndex());
                    handleRemoveProject(event, project);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
    }

    /**
     * Handles the back button action.
     *
     * @param event the ActionEvent
     */
    private void handleBack(ActionEvent event) {
        // Implement navigation logic to go back to the previous view
        navigateToPreviousView();
    }

    /**
     * Handles adding a new member to the team.
     *
     * @param event the ActionEvent
     */
    private void handleAddMember(ActionEvent event) {
        // Implement logic to add a new member, e.g., open a dialog
        Optional<AppUserDTO> result = showMemberDialog(null);
        result.ifPresent(member -> teamViewModel.addMember(member));
    }

    /**
     * Handles editing an existing member.
     *
     * @param event  the ActionEvent
     * @param member the AppUserDTO to edit
     */
    private void handleEditMember(ActionEvent event, AppUserDTO member) {
        // Implement logic to edit the member, e.g., open a dialog
        Optional<AppUserDTO> result = showMemberDialog(member);
        result.ifPresent(updatedMember -> {
            // Update member in the ViewModel or service
            teamViewModel.updateMember(updatedMember);
        });
    }

    /**
     * Handles removing a member from the team.
     *
     * @param event  the ActionEvent
     * @param member the AppUserDTO to remove
     */
    private void handleRemoveMember(ActionEvent event, AppUserDTO member) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Member");
        alert.setHeaderText("Are you sure you want to remove this member?");
        alert.setContentText("Member: " + member.getName());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            teamViewModel.removeMember(member.getId());
        }
    }

    /**
     * Handles adding a new project to the team.
     *
     * @param event the ActionEvent
     */
    private void handleAddProject(ActionEvent event) {
        // Implement logic to add a new project, e.g., open a dialog
        Optional<ProjectDTO> result = showProjectDialog(null);
        result.ifPresent(project -> teamViewModel.addProject(project));
    }

    /**
     * Handles editing an existing project.
     *
     * @param event   the ActionEvent
     * @param project the ProjectDTO to edit
     */
    private void handleEditProject(ActionEvent event, ProjectDTO project) {
        // Implement logic to edit the project, e.g., open a dialog
        Optional<ProjectDTO> result = showProjectDialog(project);
        result.ifPresent(updatedProject -> {
            // Update project in the ViewModel or service
            teamViewModel.updateProject(updatedProject);
        });
    }

    /**
     * Handles removing a project from the team.
     *
     * @param event   the ActionEvent
     * @param project the ProjectDTO to remove
     */
    private void handleRemoveProject(ActionEvent event, ProjectDTO project) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Project");
        alert.setHeaderText("Are you sure you want to remove this project?");
        alert.setContentText("Project: " + project.getName());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            teamViewModel.removeProject(project.getId());
        }
    }

    /**
     * Shows a dialog to add or edit a member.
     *
     * @param member the AppUserDTO to edit, or null to add a new member
     * @return an Optional containing the updated AppUserDTO if confirmed
     */
    private Optional<AppUserDTO> showMemberDialog(AppUserDTO member) {
        // Implement the dialog logic
        // Return Optional.of(updatedMember) if user confirms, otherwise Optional.empty()
        return Optional.empty();
    }

    /**
     * Shows a dialog to add or edit a project.
     *
     * @param project the ProjectDTO to edit, or null to add a new project
     * @return an Optional containing the updated ProjectDTO if confirmed
     */
    private Optional<ProjectDTO> showProjectDialog(ProjectDTO project) {
        // Implement the dialog logic
        // Return Optional.of(updatedProject) if user confirms, otherwise Optional.empty()
        return Optional.empty();
    }

    /**
     * Navigates back to the previous view.
     */
    private void navigateToPreviousView() {
        // Implement navigation logic
    }
}