package com.projecthub.ui.controllers;

import org.springframework.stereotype.Component;

import com.projecthub.model.Submission;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controller for displaying Submission details.
 */
@Component
public class SubmissionDetailsController {

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
            // submissionService.saveSubmission(submission);
        } catch (NumberFormatException e) {
            // Handle invalid grade input
        }
    }
}