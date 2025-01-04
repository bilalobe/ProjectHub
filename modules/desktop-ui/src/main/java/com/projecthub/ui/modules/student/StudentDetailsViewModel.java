package com.projecthub.ui.modules.student;


import com.projecthub.base.student.api.dto.SubmissionDTO;
import com.projecthub.base.student.api.mapper.SubmissionMapper;
import com.projecthub.base.student.application.service.SubmissionService;
import com.projecthub.base.student.domain.entity.Student;
import com.projecthub.base.student.domain.entity.Submission;
import com.projecthub.ui.shared.utils.MappingUtils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * ViewModel for managing student details.
 */
@Component
public class StudentDetailsViewModel {

    private static final Logger logger = LoggerFactory.getLogger(StudentDetailsViewModel.class);

    private final SubmissionService submissionService;
    private final SubmissionMapper submissionMapper;

    private final SimpleObjectProperty<UUID> studentId = new SimpleObjectProperty<>();
    private final SimpleStringProperty studentName = new SimpleStringProperty();

    private final ObservableList<Submission> submissions = FXCollections.observableArrayList();

    /**
     * Constructor with dependency injection.
     *
     * @param submissionService the submission service
     * @param submissionMapper  the submission mapper
     */
    public StudentDetailsViewModel(SubmissionService submissionService, SubmissionMapper submissionMapper) {
        this.submissionService = submissionService;
        this.submissionMapper = submissionMapper;
    }

    public SimpleObjectProperty<UUID> studentIdProperty() {
        return studentId;
    }

    public SimpleStringProperty studentNameProperty() {
        return studentName;
    }

    public ObservableList<Submission> getSubmissions() {
        return submissions;
    }

    /**
     * Sets the current student and loads related submissions.
     *
     * @param student the student entity
     */
    public void setStudent(Student student) {
        if (student == null) {
            logger.warn("Student is null in setStudent()");
            return;
        }

        studentId.set(student.getId());
        studentName.set(student.getFirstName());
        loadSubmissions(student.getId());
    }

    /**
     * Retrieves submissions by student ID.
     *
     * @param studentId the student ID
     * @return list of submission DTOs
     */
    public List<SubmissionDTO> getSubmissionsByStudentId(UUID studentId) {
        if (studentId == null) {
            logger.warn("StudentId is null in getSubmissionsByStudentId()");
            return List.of();
        }
        logger.info("Retrieving submissions for student ID: {}", studentId);
        return submissionService.getSubmissionsByStudentId(studentId);
    }

    /**
     * Maps SubmissionDTO to Submission entity.
     *
     * @param submissionDTO the submission DTO
     * @return the submission entity
     */
    public Submission mapToSubmission(SubmissionDTO submissionDTO) {
        return submissionMapper.toSubmission(submissionDTO);
    }

    private void loadSubmissions(UUID studentId) {
        if (studentId == null) {
            logger.warn("StudentId is null in loadSubmissions()");
            return;
        }
        try {
            List<SubmissionDTO> submissionSummaries = getSubmissionsByStudentId(studentId);
            List<Submission> submissionList = submissionSummaries.stream()
                .map(this::mapToSubmission)
                .toList();
            submissions.setAll(submissionList);
        } catch (Exception e) {
            logger.error("Failed to load submissions for student ID: {}", studentId, e);
        }
    }

    public void updateDetails(Submission submission) {
        MappingUtils.mapToStudentDetails(submission);
    }
}
