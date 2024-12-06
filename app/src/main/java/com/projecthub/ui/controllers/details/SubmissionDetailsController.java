package com.projecthub.ui.controllers.details;

import org.springframework.stereotype.Component;

import com.projecthub.dto.SubmissionDTO;
import com.projecthub.ui.controllers.BaseController;
import com.projecthub.ui.viewmodels.details.SubmissionDetailsViewModel;

import java.util.UUID;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controller for handling submission details.
 */
@Component
public class SubmissionDetailsController extends BaseController {

    private final SubmissionDetailsViewModel submissionViewModel;

    @FXML
    private Label submissionIdLabel;

    @FXML
    private Label studentNameLabel;

    @FXML
    private Label projectNameLabel;

    @FXML
    private Label timestampLabel;

    @FXML
    private TextField gradeField;

    @FXML
    private TextArea contentArea;

    @FXML
    private Button saveSubmissionButton;

    @FXML
    private Button deleteSubmissionButton;

    /**
     * Constructor with dependencies injected.
     *
     * @param submissionViewModel the SubmissionDetailsViewModel instance
     */
    public SubmissionDetailsController(SubmissionDetailsViewModel submissionViewModel) {
        this.submissionViewModel = submissionViewModel;
    }

    @FXML
    public void initialize() {
        submissionIdLabel.textProperty().bind(submissionViewModel.submissionIdProperty().asString());
        studentNameLabel.textProperty().bind(submissionViewModel.studentNameProperty());
        projectNameLabel.textProperty().bind(submissionViewModel.projectNameProperty());
        timestampLabel.textProperty().bind(submissionViewModel.timestampProperty());
        gradeField.textProperty().bindBidirectional(submissionViewModel.gradeProperty());
        contentArea.textProperty().bindBidirectional(submissionViewModel.contentProperty());

        saveSubmissionButton.setOnAction(this::saveSubmission);
        deleteSubmissionButton.setOnAction(this::deleteSubmission);
    }

    public void setSubmission(SubmissionDTO submission) {
        submissionViewModel.setSubmission(submission);
    }

    @FXML
    private void saveSubmission(ActionEvent event) {
        // ...enhanced input validation...
        try {
            SubmissionDTO submissionSummary = new SubmissionDTO(
            );
            submissionViewModel.saveSubmission(submissionSummary);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Submission saved successfully.");
        } catch (Exception e) {
            logger.error("Failed to save submission", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save submission.");
        }
    }

    @FXML
    private void deleteSubmission(ActionEvent event) {
        try {
            UUID submissionId = submissionViewModel.submissionIdProperty().get();
            if (submissionId == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "No submission selected.");
                return;
            }
            submissionViewModel.deleteSubmission(submissionId);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Submission deleted successfully.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete submission: " + e.getMessage());
        }
    }

    protected void showAlert(Alert.AlertType alertType, String title, String message) {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
}