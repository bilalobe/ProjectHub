package com.projecthub.ui.controllers.details;

import org.springframework.stereotype.Component;

import com.projecthub.dto.SubmissionDTO;
import com.projecthub.ui.controllers.BaseController;
import com.projecthub.ui.viewmodels.details.SubmissionDetailsViewModel;
import com.projecthub.mapper.SubmissionMapper;

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
    private final SubmissionMapper submissionMapper;

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
     * @param submissionMapper    the SubmissionMapper instance
     */
    public SubmissionDetailsController(SubmissionDetailsViewModel submissionViewModel, SubmissionMapper submissionMapper) {
        this.submissionViewModel = submissionViewModel;
        this.submissionMapper = submissionMapper;
    }

    @FXML
    public void initialize() {
        bindProperties();
        setupEventHandlers();
    }

    private void bindProperties() {
        submissionIdLabel.textProperty().bind(submissionViewModel.submissionIdProperty().asString());
        studentNameLabel.textProperty().bind(submissionViewModel.studentNameProperty());
        projectNameLabel.textProperty().bind(submissionViewModel.projectNameProperty());
        timestampLabel.textProperty().bind(submissionViewModel.timestampProperty());
        gradeField.textProperty().bindBidirectional(submissionViewModel.gradeProperty());
        contentArea.textProperty().bindBidirectional(submissionViewModel.contentProperty());
    }

    private void setupEventHandlers() {
        saveSubmissionButton.setOnAction(this::saveSubmission);
        deleteSubmissionButton.setOnAction(this::deleteSubmission);
    }

    public void setSubmission(SubmissionDTO submission) {
        submissionViewModel.setSubmission(submission);
    }

    @FXML
    private void saveSubmission(ActionEvent event) {
        try {
            // Create a new SubmissionDTO using the mapper
            SubmissionDTO submissionSummary = submissionMapper.toSubmissionDTO(submissionViewModel);

            // Save the submission
            submissionViewModel.saveSubmission(submissionSummary);

            // Show success alert
            showAlert(Alert.AlertType.INFORMATION, "Success", "Submission saved successfully.");
        } catch (Exception e) {
            // Log the error and show error alert
            logError("Failed to save submission", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save submission: " + e.getMessage());
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
            logError("Failed to delete submission", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete submission: " + e.getMessage());
        }
    }
}