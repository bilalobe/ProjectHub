package com.projecthub.ui.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.projecthub.dto.AppUserSummary;
import com.projecthub.model.AppUser;
import com.projecthub.model.Project;
import com.projecthub.model.Team;
import com.projecthub.ui.viewmodels.TeamViewModel;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

/**
 * Controller for displaying Team details.
 */
@Component
public class TeamDetailsController {

    @Autowired
    private TeamViewModel teamViewModel;

    @FXML
    private Label teamNameLabel;

    @FXML
    private Label cohortNameLabel;

    @FXML
    private TableView<Project> projectTableView;

    @FXML
    private TableColumn<Project, Long> projectIdColumn;

    @FXML
    private TableColumn<Project, String> projectNameColumn;

    @FXML
    private TableView<AppUser> memberTableView;

    @FXML
    private TableColumn<AppUser, Long> memberIdColumn;

    @FXML
    private TableColumn<AppUser, String> memberNameColumn;

    @FXML
    private Button addProjectButton;

    @FXML
    private Button addMemberButton;

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        // Bind labels to ViewModel properties
        teamNameLabel.textProperty().bind(teamViewModel.teamNameProperty());
        cohortNameLabel.textProperty().bind(teamViewModel.cohortNameProperty());

        // Initialize Project TableView
        projectIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        projectNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        projectTableView.setItems(teamViewModel.getProjects());

        // Initialize Member TableView
        memberIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        memberNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        memberTableView.setItems(teamViewModel.getMembers());
    }

    /**
     * Sets the Team to display.
     *
     * @param team the Team
     */
    public void setTeam(Team team) {
        teamViewModel.setTeam(team);
    }

    /**
     * Handles the action of adding a new Project.
     */
    @FXML
    private void handleAddProject() {
        Dialog<Project> dialog = new Dialog<>();
        dialog.setTitle("Add New Project");
        dialog.setHeaderText("Enter project details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = createProjectDialogGrid();

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a Project when the Add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                TextField nameField = (TextField) grid.lookup("#nameField");
                TextArea descriptionArea = (TextArea) grid.lookup("#descriptionArea");
                TextField deadlineField = (TextField) grid.lookup("#deadlineField");

                String name = nameField.getText();
                String description = descriptionArea.getText();
                String deadline = deadlineField.getText();

                Project project = new Project();
                project.setName(name);
                project.setDescription(description);
                project.setDeadline(deadline);
                return project;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(project -> {
            teamViewModel.addProject(project);
        });
    }

    /**
     * Handles the action of adding a new Member.
     */
    @FXML
    private void handleAddMember() {
        Dialog<AppUser> dialog = new Dialog<>();
        dialog.setTitle("Add New Member");
        dialog.setHeaderText("Enter member details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = createMemberDialogGrid();

        dialog.getDialogPane().setContent(grid);

        // Convert the result to an AppUser when the Add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                TextField usernameField = (TextField) grid.lookup("#usernameField");
                PasswordField passwordField = (PasswordField) grid.lookup("#passwordField");

                String username = usernameField.getText();
                String password = passwordField.getText();

                AppUser user = new AppUser();
                user.setUsername(username);
                user.setPassword(password);
                return user;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(user -> {
            AppUserSummary userSummary = new AppUserSummary(user.getId(), user.getUsername(), user.getPassword(), user.getTeam().getId());
            teamViewModel.addMember(userSummary);
        });
    }

    /**
     * Creates the GridPane for the Project dialog.
     *
     * @return GridPane
     */
    private GridPane createProjectDialogGrid() {
        GridPane grid = new GridPane();
        grid.setId("projectGrid");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setId("nameField");
        nameField.setPromptText("Project Name");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setId("descriptionArea");
        descriptionArea.setPromptText("Project Description");

        TextField deadlineField = new TextField();
        deadlineField.setId("deadlineField");
        deadlineField.setPromptText("Deadline");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);
        grid.add(new Label("Deadline:"), 0, 2);
        grid.add(deadlineField, 1, 2);

        return grid;
    }

    /**
     * Creates the GridPane for the Member dialog.
     *
     * @return GridPane
     */
    private GridPane createMemberDialogGrid() {
        GridPane grid = new GridPane();
        grid.setId("memberGrid");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField usernameField = new TextField();
        usernameField.setId("usernameField");
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setId("passwordField");
        passwordField.setPromptText("Password");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);

        return grid;
    }
}