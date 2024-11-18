package com.projecthub.ui.controllers;

import com.projecthub.dto.ProjectSummary;
import com.projecthub.model.AppUser;
import com.projecthub.model.Project;
import com.projecthub.model.Team;
import com.projecthub.service.ProjectService;
import com.projecthub.service.UserService;
import com.projecthub.ui.viewmodels.TeamViewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TeamDetailsController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

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

    private Team team;

    public void setTeam(Team team) {
        this.team = team;
        updateUI();
    }

    @FXML
    public void initialize() {
        projectIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        projectNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        memberIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        memberNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUsername()));
    }

    private void updateUI() {
        if (team != null) {
            teamNameLabel.setText(team.getName());
            cohortNameLabel.setText(team.getCohort().getName());
            loadProjects();
            loadMembers();
        }
    }

    private void loadProjects() {
        List<ProjectSummary> projectSummaries = projectService.getProjectsByTeamId(team.getId());
        List<Project> projects = projectSummaries.stream()
                .map(summary -> new Project(summary.getId(), summary.getName(), summary.getDescription(), team))
                .collect(Collectors.toList());
        ObservableList<Project> projectList = FXCollections.observableArrayList(projects);
        projectTableView.setItems(projectList);
    }

    private void loadMembers() {
        List<AppUser> members = userService.getUsersByTeamId(team.getId());
        ObservableList<AppUser> memberList = FXCollections.observableArrayList(members);
        memberTableView.setItems(memberList);
    }

    @FXML
    private void handleAddProject() {
        Dialog<Project> dialog = new Dialog<>();
        dialog.setTitle("Add New Project");
        dialog.setHeaderText("Enter project details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Project Name");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Project Description");
        TextField deadlineField = new TextField();
        deadlineField.setPromptText("Deadline");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);
        grid.add(new Label("Deadline:"), 0, 2);
        grid.add(deadlineField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String name = nameField.getText();
                String description = descriptionArea.getText();
                String deadline = deadlineField.getText();
                return new Project(name, description, deadline, team);
            }
            return null;
        });

        Optional<Project> result = dialog.showAndWait();
        result.ifPresent(project -> {
            projectService.saveProject(project);
            loadProjects();
        });
    }

    @FXML
    private void handleAddMember() {
        Dialog<AppUser> dialog = new Dialog<>();
        dialog.setTitle("Add New Member");
        dialog.setHeaderText("Enter member details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String username = usernameField.getText();
                String password = passwordField.getText();
                AppUser user = new AppUser(username, password, team);
                return user;
            }
            return null;
        });

        Optional<AppUser> result = dialog.showAndWait();
        result.ifPresent(user -> {
            userService.saveUser(user);
            loadMembers();
        });
    }
}