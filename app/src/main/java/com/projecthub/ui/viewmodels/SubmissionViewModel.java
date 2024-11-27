package com.projecthub.ui.viewmodels;

import com.projecthub.dto.SubmissionSummary;
import com.projecthub.service.SubmissionService;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;

@Component
public class SubmissionViewModel {

    private final SubmissionService submissionService;

    private final SimpleLongProperty submissionId = new SimpleLongProperty();
    private final SimpleStringProperty studentName = new SimpleStringProperty();
    private final SimpleStringProperty projectName = new SimpleStringProperty();
    private final SimpleStringProperty timestamp = new SimpleStringProperty();
    private final SimpleStringProperty grade = new SimpleStringProperty();
    private final SimpleStringProperty content = new SimpleStringProperty();

    private final ObservableList<SubmissionSummary> submissions = FXCollections.observableArrayList();
    
    public SubmissionViewModel(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    public SimpleLongProperty submissionIdProperty() {
        return submissionId;
    }

    public SimpleStringProperty studentNameProperty() {
        return studentName;
    }

    public SimpleStringProperty projectNameProperty() {
        return projectName;
    }

    public SimpleStringProperty timestampProperty() {
        return timestamp;
    }

    public SimpleStringProperty gradeProperty() {
        return grade;
    }

    public SimpleStringProperty contentProperty() {
        return content;
    }

    public ObservableList<SubmissionSummary> getSubmissions() {
        return submissions;
    }

    public void setSubmission(SubmissionSummary submission) {
        if (submission != null) {
            submissionId.set(submission.getId());
            studentName.set(submission.getStudentName());
            projectName.set(submission.getProjectName());
            timestamp.set(submission.getTimestamp());
            grade.set(submission.getGrade() != null ? submission.getGrade().toString() : "Not Graded");
            content.set(submission.getContent());
        }
    }

    public void loadSubmissions() {
        submissions.setAll(submissionService.getAllSubmissions());
    }

    public void saveSubmission(SubmissionSummary submissionSummary) {
        submissionService.saveSubmission(submissionSummary);
        loadSubmissions();
    }

    public void deleteSubmission(Long submissionId) {
        submissionService.deleteSubmission(submissionId);
        loadSubmissions();
    }

    public void clearSubmission() {
        submissionId.set(0);
        studentName.set("");
        projectName.set("");
        timestamp.set("");
        grade.set("");
        content.set("");
    }
}