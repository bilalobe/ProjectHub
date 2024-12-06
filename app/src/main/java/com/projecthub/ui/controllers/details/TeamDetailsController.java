package com.projecthub.ui.controllers.details;

import com.projecthub.dto.TeamSummary;
import com.projecthub.dto.ProjectSummary;
import com.projecthub.dto.AppUserSummary;
import com.projecthub.ui.viewmodels.TeamViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

@Component
public class TeamDetailsController {

    
    private TeamViewModel teamViewModel;

    @FXML
    private Label teamIdLabel;

    @FXML
    private Label teamNameLabel;

    @FXML
    private Label cohortNameLabel;

    @FXML
    private TableView<ProjectSummary> projectsTableView;

    @FXML
    private TableColumn<ProjectSummary, Long> projectIdColumn;

    @FXML
    private TableColumn<ProjectSummary, String> projectNameColumn;

    @FXML
    private TableView<AppUserSummary> membersTableView;

    @FXML
    private TableColumn<AppUserSummary, Long> memberIdColumn;

    @FXML
    private TableColumn<AppUserSummary, String> memberNameColumn;

    @FXML
    private Button saveTeamButton;

    @FXML
    private Button deleteTeamButton;

    @FXML
    public void initialize() {
        teamIdLabel.textProperty().bind(teamViewModel.teamIdProperty().asString());
        teamNameLabel.textProperty().bind(teamViewModel.teamNameProperty());
        cohortNameLabel.textProperty().bind(teamViewModel.cohortNameProperty());

        projectIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        projectNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        memberIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        memberNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        projectsTableView.setItems(teamViewModel.getProjects());
        membersTableView.setItems(teamViewModel.getMembers());

        saveTeamButton.setOnAction(event -> saveTeam());
        deleteTeamButton.setOnAction(event -> deleteTeam());
    }

    public void setTeam(TeamSummary team) {
        teamViewModel.setTeam(team);
    }

    private void saveTeam() {
        TeamSummary teamSummary = new TeamSummary(
                teamViewModel.teamIdProperty().get(),
                teamViewModel.teamNameProperty().get(),
                null, // schoolId
                null, // cohortId
                null, // schoolName
                teamViewModel.cohortNameProperty().get()
        );
        teamViewModel.saveTeam(teamSummary);
    }

    private void deleteTeam() {
        teamViewModel.deleteTeam(teamViewModel.teamIdProperty().get());
    }
}