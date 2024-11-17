package com.projecthub.ui.controllers;

import com.projecthub.model.Team;
import com.projecthub.dto.ProjectSummary;
import com.projecthub.model.Project;
import com.projecthub.model.AppUser;
import com.projecthub.service.ProjectService;
import com.projecthub.service.UserService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for displaying Team details.
 */
@Component
public class TeamDetailsController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

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

    /**
     * Sets the Team to display.
     *
     * @param team the Team
     */
    public void setTeam(Team team) {
        this.team = team;
        updateUI();
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        projectIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        projectNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        memberIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        memberNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUsername()));
    }

    /**
     * Updates the UI elements with Team data.
     */
    private void updateUI() {
        if (team != null) {
            teamNameLabel.setText(team.getName());
            cohortNameLabel.setText(team.getCohort().getName());
            loadProjects();
            loadMembers();
        }
    }

    /**
     * Loads the projects associated with the Team.
     */
    private void loadProjects() {
        List<ProjectSummary> projectSummaries = projectService.getProjectsByTeamId(team.getId());
        List<Project> projects = projectSummaries.stream()
                .map(summary -> new Project(summary.getId(), summary.getName(), null, team))
                .collect(Collectors.toList());
        ObservableList<Project> projectList = FXCollections.observableArrayList(projects);
        projectTableView.setItems(projectList);
    }

    /**
     * Loads the members of the Team.
     */
    private void loadMembers() {
        List<AppUser> members = userService.getUsersByTeamId(team.getId());
        ObservableList<AppUser> memberList = FXCollections.observableArrayList(members);
        memberTableView.setItems(memberList);
    }

    /**
     * Handles the action of adding a new Project.
     */
    @FXML
    private void handleAddProject() {
        // Implement adding a new project to the team
    }

    /**
     * Handles the action of adding a new Member.
     */
    @FXML
    private void handleAddMember() {
        // Implement adding a new member to the team
    }
}