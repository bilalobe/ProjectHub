package com.projecthub.ui.controllers.details;

import org.springframework.stereotype.Component;

import com.projecthub.dto.SubmissionSummary;
import com.projecthub.ui.viewmodels.SubmissionViewModel;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

@Component
public class SubmissionDetailsController {

    
    private SubmissionViewModel submissionViewModel;

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

    @FXML
    public void initialize() {
        submissionIdLabel.textProperty().bind(submissionViewModel.submissionIdProperty().asString());
        studentNameLabel.textProperty().bind(submissionViewModel.studentNameProperty());
        projectNameLabel.textProperty().bind(submissionViewModel.projectNameProperty());
        timestampLabel.textProperty().bind(submissionViewModel.timestampProperty());
        gradeField.textProperty().bindBidirectional(submissionViewModel.gradeProperty());
        contentArea.textProperty().bindBidirectional(submissionViewModel.contentProperty());

        saveSubmissionButton.setOnAction(event -> saveSubmission());
        deleteSubmissionButton.setOnAction(event -> deleteSubmission());
    }

    public void setSubmission(SubmissionSummary submission) {
        submissionViewModel.setSubmission(submission);
    }

    private void saveSubmission() {
        SubmissionSummary submissionSummary = new SubmissionSummary(
                submissionViewModel.submissionIdProperty().get(),
                submissionViewModel.contentProperty().get(),
                submissionViewModel.timestampProperty().get(),
                Integer.valueOf(submissionViewModel.gradeProperty().get()),
                null, // projectId
                null, // studentId
                null, // additionalField1
                null  // additionalField2
        );
        submissionViewModel.saveSubmission(submissionSummary);
    }

    private void deleteSubmission() {
        submissionViewModel.deleteSubmission(submissionViewModel.submissionIdProperty().get());
    }

    @SuppressWarnings("unused")
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}