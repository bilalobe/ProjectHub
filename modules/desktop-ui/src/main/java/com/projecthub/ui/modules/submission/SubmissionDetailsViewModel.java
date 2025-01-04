package com.projecthub.ui.modules.submission;

import com.projecthub.base.student.api.dto.SubmissionDTO;
import com.projecthub.base.student.application.service.StudentService;
import com.projecthub.base.student.application.service.SubmissionService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * ViewModel for managing submission details.
 */
@Component
public class SubmissionDetailsViewModel {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionDetailsViewModel.class);

    private final SubmissionService submissionService;

    private final SimpleObjectProperty<UUID> submissionId = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<UUID> projectId = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<UUID> studentId = new SimpleObjectProperty<>();
    private final SimpleStringProperty studentName = new SimpleStringProperty();
    private final SimpleStringProperty studentFirstName = new SimpleStringProperty();
    private final SimpleStringProperty studentLastName = new SimpleStringProperty();
    private final SimpleStringProperty projectName = new SimpleStringProperty();
    private final SimpleStringProperty timestamp = new SimpleStringProperty();
    private final SimpleStringProperty grade = new SimpleStringProperty();
    private final SimpleStringProperty content = new SimpleStringProperty();

    private final ObservableList<SubmissionDTO> submissions = FXCollections.observableArrayList();

    /**
     * Constructor with dependency injection.
     *
     * @param submissionService the submission service
     * @param studentService    the student service
     */
    public SubmissionDetailsViewModel(SubmissionService submissionService, StudentService studentService) {
        this.submissionService = submissionService;
    }

    public SimpleObjectProperty<UUID> submissionIdProperty() {
        return submissionId;
    }

    public SimpleObjectProperty<UUID> projectIdProperty() {
        return projectId;
    }

    public SimpleObjectProperty<UUID> studentIdProperty() {
        return studentId;
    }

    public SimpleStringProperty studentNameProperty() {
        return studentName;
    }

    public SimpleStringProperty studentFirstNameProperty() {
        return studentFirstName;
    }

    public SimpleStringProperty studentLastNameProperty() {
        return studentLastName;
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

    public ObservableList<SubmissionDTO> getSubmissions() {
        return submissions;
    }

    // Add getter methods for MapStruct
    public UUID getSubmissionId() {
        return submissionId.get();
    }

    public UUID getProjectId() {
        return projectId.get();
    }

    public UUID getStudentId() {
        return studentId.get();
    }

    public String getStudentName() {
        return studentName.get();
    }

    public String getStudentFirstName() {
        return studentFirstName.get();
    }

    public String getStudentLastName() {
        return studentLastName.get();
    }

    public String getProjectName() {
        return projectName.get();
    }

    public String getTimestamp() {
        return timestamp.get();
    }

    public String getGrade() {
        return grade.get();
    }

    public String getContent() {
        return content.get();
    }

    /**
     * Sets the current submission and loads related data.
     *
     * @param submission the submission DTO
     */
    public void setSubmission(SubmissionDTO submission) {
        if (submission == null) {
            logger.warn("Submission is null in setSubmission()");
            clearSubmission();
            return;
        }

        submissionId.set(submission.getId());
        projectId.set(submission.getProjectId());
        studentId.set(submission.getStudentId());
        projectName.set(submission.getProjectName());
        timestamp.set(submission.getTimestamp().toString());
        grade.set(submission.getGrade() != null ? submission.getGrade().toString() : "");
        content.set(submission.getContent());
        studentFirstName.set(submission.getStudentFirstName());
        studentLastName.set(submission.getStudentLastName());
        studentName.set(submission.getStudentFirstName() + " " + submission.getStudentLastName());
    }

    /**
     * Saves the submission.
     *
     * @param submissionSummary the submission DTO
     */
    public void saveSubmission(SubmissionDTO submissionSummary) {
        if (submissionSummary == null) {
            logger.warn("SubmissionSummary is null in saveSubmission()");
            return;
        }
        try {
            if (submissionSummary.getId() != null) {
                submissionService.updateSubmission(submissionSummary.getId(), submissionSummary);
            } else {
                submissionService.createSubmission(submissionSummary);
            }
            setSubmission(submissionSummary);
        } catch (Exception e) {
            logger.error("Failed to save submission: {}", submissionSummary, e);
        }
    }

    /**
     * Deletes the submission by ID.
     *
     * @param submissionId the submission ID
     */
    public void deleteSubmission(UUID submissionId) {
        if (submissionId == null) {
            logger.warn("SubmissionId is null in deleteSubmission()");
            return;
        }
        try {
            submissionService.deleteSubmission(submissionId);
            clearSubmission();
        } catch (Exception e) {
            logger.error("Failed to delete submission ID: {}", submissionId, e);
        }
    }

    /**
     * Clears the current submission data.
     */
    public void clearSubmission() {
        submissionId.set(null);
        projectId.set(null);
        studentId.set(null);
        studentName.set("");
        studentFirstName.set("");
        studentLastName.set("");
        projectName.set("");
        timestamp.set("");
        grade.set("");
        content.set("");
    }


    public void updateDetails(Submission submission) {
        if (submission == null) {
            throw new IllegalArgumentException("Submission cannot be null");
        }
        // ...existing code...
    }

    public void updateDetails(Submission submission) {
        MappingUtils.mapToSubmissionDetails(submission);
    }
}
