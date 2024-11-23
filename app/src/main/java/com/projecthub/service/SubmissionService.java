package com.projecthub.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projecthub.dto.SubmissionSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.mapper.SubmissionMapper;
import com.projecthub.model.Submission;
import com.projecthub.repository.custom.CustomSubmissionRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Service
@Tag(name = "Submission Service", description = "Operations pertaining to submissions in ProjectHub")
public class SubmissionService {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);

    private final CustomSubmissionRepository submissionRepository;

    public SubmissionService(@Qualifier("csvSubmissionRepository") CustomSubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
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
                .map(SubmissionMapper::toSubmissionSummary)
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
        Submission submission = SubmissionMapper.toSubmission(submissionSummary);
        submissionRepository.save(submission);
        logger.info("Submission saved");
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
        if (!submissionRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException("Submission not found with ID: " + id);
        }
        submissionRepository.deleteById(id);
        logger.info("Submission deleted");
    }

    /**
     * Retrieves submissions by student ID.
     *
     * @param id the student ID
     * @return a list of SubmissionSummary
     * @throws IllegalArgumentException if id is null
     */
    public List<SubmissionSummary> getSubmissionsByStudentId(Long id) {
        logger.info("Retrieving submissions for student ID {}", id);
        if (id == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }
        return submissionRepository.findByStudentId(id).stream()
                .map(SubmissionMapper::toSubmissionSummary)
                .collect(Collectors.toList());
    }
}