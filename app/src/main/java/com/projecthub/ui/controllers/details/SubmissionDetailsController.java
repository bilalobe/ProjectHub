package com.projecthub.ui.controllers.details;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.projecthub.dto.SubmissionSummary;
import com.projecthub.model.Submission;
import com.projecthub.service.SubmissionService;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controller for displaying Submission details.
 */
@Component
public class SubmissionDetailsController {

    @Autowired
    private SubmissionService submissionService;

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

    private Submission submission;

    /**
     * Sets the Submission to display.
     *
     * @param submission the Submission
     */
    public void setSubmission(Submission submission) {
        this.submission = submission;
        updateUI();
    }

    /**
     * Updates the UI elements with Submission data.
     */
    private void updateUI() {
        if (submission != null) {
            submissionIdLabel.setText(submission.getId().toString());
            studentNameLabel.setText(submission.getStudent().getName());
            projectNameLabel.setText(submission.getProject().getName());
            timestampLabel.setText(submission.getTimestamp());
            gradeField.setText(submission.getGrade() != null ? submission.getGrade().toString() : "");
            contentArea.setText(submission.getContent());
        }
    }

    /**
     * Handles the action of saving the grade.
     */
    @FXML
    private void handleSaveGrade() {
        String gradeText = gradeField.getText();
        try {
            Integer grade = Integer.valueOf(gradeText);
            submission.setGrade(grade);
            // Save submission with new grade
            SubmissionSummary submissionSummary = new SubmissionSummary(submission);
            submissionService.saveSubmission(submissionSummary);
            showAlert(AlertType.INFORMATION, "Grade Saved", "The grade has been successfully saved.");
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid Grade", "Please enter a valid integer for the grade.");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "An error occurred while saving the grade. Please try again.");
        }
    }

    /**
     * Shows an alert with the specified type, title, and message.
     *
     * @param alertType the type of the alert
     * @param title     the title of the alert
     * @param message   the message of the alert
     */
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}