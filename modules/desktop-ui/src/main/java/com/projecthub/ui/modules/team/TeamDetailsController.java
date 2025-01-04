package com.projecthub.ui.modules.team;

import com.projecthub.base.project.api.dto.ProjectDTO;
import com.projecthub.base.shared.utils.UUIDStringConverter;
import com.projecthub.base.team.api.dto.TeamDTO;
import com.projecthub.base.users.api.dto.AppUserDTO;
import com.projecthub.ui.shared.utils.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Controller for handling team details UI interactions.
 */
@Component
public class TeamDetailsController extends BaseController {

    private final TeamDetailsViewModel teamViewModel;

    @FXML
    private Label teamIdLabel;

    @FXML
    private TextField teamNameField;

    @FXML
    private TextField cohortNameField;

    @FXML
    private TextField schoolIdField;

    @FXML
    private TextField cohortIdField;

    @FXML
    private TableView<ProjectDTO> projectsTableView;

    @FXML
    private TableColumn<ProjectDTO, UUID> projectIdColumn;

    @FXML
    private TableColumn<ProjectDTO, String> projectNameColumn;

    @FXML
    private TableView<AppUserDTO> membersTableView;

    @FXML
    private TableColumn<AppUserDTO, UUID> memberIdColumn;

    @FXML
    private TableColumn<AppUserDTO, String> memberNameColumn;

    @FXML
    private Button saveTeamButton;

    @FXML
    private Button deleteTeamButton;

    /**
     * Constructor with dependencies injected.
     *
     * @param teamViewModel the TeamDetailsViewModel instance
     */
    public TeamDetailsController(TeamDetailsViewModel teamViewModel) {
        this.teamViewModel = teamViewModel;
    }

    @FXML
    public void initialize() {
        teamIdLabel.textProperty().bind(teamViewModel.teamIdProperty().asString());
        teamNameField.textProperty().bindBidirectional(teamViewModel.teamNameProperty());
        cohortNameField.textProperty().bindBidirectional(teamViewModel.cohortNameProperty());

        UUIDStringConverter uuidStringConverter = new UUIDStringConverter();
        schoolIdField.textProperty().bindBidirectional(teamViewModel.schoolIdProperty(), uuidStringConverter);
        cohortIdField.textProperty().bindBidirectional(teamViewModel.cohortIdProperty(), uuidStringConverter);

        projectIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        projectNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        memberIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        memberNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        projectsTableView.setItems(teamViewModel.getProjects());
        membersTableView.setItems(teamViewModel.getMembers());

        saveTeamButton.setOnAction(this::saveTeam);
        deleteTeamButton.setOnAction(this::deleteTeam);
    }

    /**
     * Sets the team to be displayed and edited.
     *
     * @param team the TeamDTO instance
     */
    public void setTeam(TeamDTO team) {
        teamViewModel.setTeam(team);
    }

    @FXML
    private void saveTeam(ActionEvent event) {
        try {
            UUID teamId = teamViewModel.teamIdProperty().get();
            String teamName = teamViewModel.teamNameProperty().get();
            UUID schoolId = teamViewModel.schoolIdProperty().get();
            UUID cohortId = teamViewModel.cohortIdProperty().get();
            teamViewModel.cohortNameProperty().get();

            if (!isValidTeamName(teamName)) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid team name provided.");
                return;
            }

            if (schoolId == null) {
                showAlert("Error", "School ID cannot be null.");
                return;
            }

            if (cohortId == null) {
                showAlert("Error", "Cohort ID cannot be null.");
                return;
            }

            TeamDTO teamSummary = new TeamDTO(
                teamId,
                teamName,
                schoolId,
                cohortId
            );

            teamViewModel.saveTeam(teamSummary);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Team saved successfully.");
            logger.info("Team saved successfully: {}", teamSummary.getId());
        } catch (Exception e) {
            logger.error("Failed to save team", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save team.");
        }
    }

    @FXML
    private void deleteTeam(ActionEvent event) {
        try {
            UUID teamId = teamViewModel.teamIdProperty().get();
            if (teamId == null) {
                showAlert("Error", "No team selected.");
                return;
            }
            teamViewModel.deleteTeam(teamId);
            showAlert("Success", "Team deleted successfully.");
        } catch (Exception e) {
            logger.error("Failed to delete team", e);
            showAlert("Error", "Failed to delete team: " + e.getMessage());
        }
    }

    private boolean isValidTeamName(String teamName) {
        return teamName != null && !teamName.trim().isEmpty();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
