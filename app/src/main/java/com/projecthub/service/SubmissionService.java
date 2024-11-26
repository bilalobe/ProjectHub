package com.projecthub.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projecthub.dto.SubmissionSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.mapper.SubmissionMapper;
import com.projecthub.model.Project;
import com.projecthub.model.Student;
import com.projecthub.model.Submission;
import com.projecthub.repository.custom.CustomProjectRepository;
import com.projecthub.repository.custom.CustomStudentRepository;
import com.projecthub.repository.custom.CustomSubmissionRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Service
@Tag(name = "Submission Service", description = "Operations pertaining to submissions in ProjectHub")
public class SubmissionService {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);

    private final CustomSubmissionRepository submissionRepository;
    private final CustomProjectRepository projectRepository;
    private final CustomStudentRepository studentRepository;

    private final SubmissionMapper submissionMapper;

    public SubmissionService(@Qualifier("csvSubmissionRepository") CustomSubmissionRepository submissionRepository,
                             @Qualifier("csvProjectRepository") CustomProjectRepository projectRepository,
                             @Qualifier("csvStudentRepository") CustomStudentRepository studentRepository,
                             SubmissionMapper submissionMapper) {
        this.submissionRepository = submissionRepository;
        this.projectRepository = projectRepository;
        this.studentRepository = studentRepository;
        this.submissionMapper = submissionMapper;
    }

    /**
     * Retrieves a list of all submissions.
     *
     * @return a list of SubmissionSummary
     */
    @Operation(summary = "View a list of all submissions")
    public List<SubmissionSummary> getAllSubmissions() {
        logger.info("Retrieving all submissions");
        return submissionRepository.findAll().stream()
                .map(submissionMapper::toSubmissionSummary)
                .collect(Collectors.toList());
    }

    /**
     * Saves a submission.
     *
     * @param submissionSummary the submission summary to save
     * @throws IllegalArgumentException if submissionSummary is null
     */
    @Operation(summary = "Save a submission")
    @Transactional
    public void saveSubmission(SubmissionSummary submissionSummary) {
        logger.info("Saving submission");
        if (submissionSummary == null) {
            throw new IllegalArgumentException("SubmissionSummary cannot be null");
        }
        Project project = findProjectById(submissionSummary.getProjectId());
        Student student = findStudentById(submissionSummary.getStudentId());
        Submission submission = submissionMapper.toSubmission(submissionSummary, project, student);
        submissionRepository.save(submission);
        logger.info("Submission saved");
    }

    /**
     * Finds a project by its ID.
     *
     * @param projectId the ID of the project
     * @return the project associated with the given ID
     * @throws ResourceNotFoundException if the project is not found
     */
    private Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));
    }

    /**
     * Finds a student by its ID.
     *
     * @param studentId the ID of the student
     * @return the student associated with the given ID
     * @throws ResourceNotFoundException if the student is not found
     */
    private Student findStudentById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
    }

    /**
     * Deletes a submission by ID.
     *
     * @param id the ID of the submission to delete
     * @throws IllegalArgumentException if id is null
     * @throws ResourceNotFoundException if the submission is not found
     */
    @Operation(summary = "Delete a submission by ID")
    @Transactional
    public void deleteSubmission(Long id) throws ResourceNotFoundException {
        logger.info("Deleting submission with ID {}", id);
        if (id == null) {
            throw new IllegalArgumentException("Submission ID cannot be null");
        }
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with ID: " + id));
        submissionRepository.delete(submission);
        logger.info("Submission deleted");
    }

    /**
     * Retrieves submissions by student ID.
     *
     * @param id the student ID
     * @return a list of SubmissionSummary
     * @throws IllegalArgumentException if id is null
     */
    @Operation(summary = "Retrieve submissions by student ID")
    public List<SubmissionSummary> getSubmissionsByStudentId(Long id) {
        logger.info("Retrieving submissions for student ID {}", id);
        if (id == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }
        return submissionRepository.findByStudentId(id).stream()
                .map(submissionMapper::toSubmissionSummary)
                .collect(Collectors.toList());
    }
}