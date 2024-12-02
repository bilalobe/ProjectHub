package com.projecthub.service;

import com.projecthub.dto.SubmissionSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.mapper.SubmissionMapper;
import com.projecthub.model.Submission;
import com.projecthub.repository.SubmissionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Tag(name = "Submission Service", description = "Operations pertaining to submissions in ProjectHub")
public class SubmissionService {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);

    private final SubmissionRepository submissionRepository;
    private final SubmissionMapper submissionMapper;


    public SubmissionService(
            @Qualifier("submissionRepository") SubmissionRepository submissionRepository,
            SubmissionMapper submissionMapper) {
        this.submissionRepository = submissionRepository;
        this.submissionMapper = submissionMapper;
    }

    @Operation(summary = "View a list of all submissions")
    public List<SubmissionSummary> getAllSubmissions() {
        logger.info("Retrieving all submissions");
        return submissionRepository.findAll().stream()
                .map(submissionMapper::toSubmissionSummary)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Retrieve a submission by ID")
    public SubmissionSummary getSubmissionById(Long id) {
        logger.info("Retrieving submission with ID {}", id);
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with ID: " + id));
        return submissionMapper.toSubmissionSummary(submission);
    }

    @Operation(summary = "Create a new submission")
    @Transactional
    public SubmissionSummary createSubmission(SubmissionSummary submissionSummary) {
        logger.info("Creating submission");
        Submission submission = submissionMapper.toSubmission(submissionSummary);
        Submission savedSubmission = submissionRepository.save(submission);
        logger.info("Submission created with ID {}", savedSubmission.getId());
        return submissionMapper.toSubmissionSummary(savedSubmission);
    }

    @Operation(summary = "Update an existing submission")
    @Transactional
    public SubmissionSummary updateSubmission(Long id, SubmissionSummary submissionSummary) {
        logger.info("Updating submission with ID {}", id);
        Submission existingSubmission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with ID: " + id));
        submissionMapper.updateSubmissionFromSummary(submissionSummary, existingSubmission);
        Submission updatedSubmission = submissionRepository.save(existingSubmission);
        logger.info("Submission updated with ID {}", updatedSubmission.getId());
        return submissionMapper.toSubmissionSummary(updatedSubmission);
    }

    @Operation(summary = "Delete a submission by ID")
    @Transactional
    public void deleteSubmission(Long id) {
        logger.info("Deleting submission with ID {}", id);
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with ID: " + id));
        submissionRepository.delete(submission);
        logger.info("Submission deleted with ID {}", id);
    }

    public void saveSubmission(SubmissionSummary submissionSummary) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}