package com.projecthub.ui.viewmodels.details;

import com.projecthub.dto.SubmissionDTO;
import com.projecthub.service.StudentService;
import com.projecthub.service.SubmissionService;
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
    private final StudentService studentService;

    private final SimpleObjectProperty<UUID> submissionId = new SimpleObjectProperty<>();
    private final SimpleStringProperty studentName = new SimpleStringProperty();
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
        this.studentService = studentService;
    }

    public SimpleObjectProperty<UUID> submissionIdProperty() {
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

    public ObservableList<SubmissionDTO> getSubmissions() {
        return submissions;
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
        projectName.set(submission.getProjectName());
        timestamp.set(submission.getTimestamp().toString());
        grade.set(submission.getGrade() != null ? submission.getGrade().toString() : "");
        content.set(submission.getContent());
        loadStudentName(submission.getStudentId());
    }

    private void loadStudentName(UUID studentId) {
        if (studentId == null) {
            logger.warn("StudentId is null in loadStudentName()");
            studentName.set("Unknown");
            return;
        }
        try {
            String name = studentService.getStudentById(studentId).getFirstName();
            studentName.set(name);
        } catch (Exception e) {
            logger.error("Failed to load student name for student ID: {}", studentId, e);
            studentName.set("Unknown");
        }
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
        studentName.set("");
        projectName.set("");
        timestamp.set("");
        grade.set("");
        content.set("");
    }
}