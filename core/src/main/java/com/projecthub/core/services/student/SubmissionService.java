package com.projecthub.core.services.student;

import com.projecthub.core.dto.SubmissionDTO;
import com.projecthub.core.exceptions.ResourceNotFoundException;
import com.projecthub.core.mappers.SubmissionMapper;
import com.projecthub.core.models.Submission;
import com.projecthub.core.repositories.jpa.SubmissionJpaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing submissions.
 */
@Service
public class SubmissionService {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);

    private final SubmissionJpaRepository submissionRepository;
    private final SubmissionMapper submissionMapper;

    private final SubmissionService self;

    public SubmissionService(SubmissionJpaRepository submissionRepository, SubmissionMapper submissionMapper, SubmissionService self) {
        this.submissionRepository = submissionRepository;
        this.submissionMapper = submissionMapper;
        this.self = self;
    }

    /**
     * Saves a new submission.
     *
     * @param submissionDTO the submission data transfer object
     * @return
     * @throws IllegalArgumentException if submissionDTO is null
     */
    @Transactional
    public SubmissionDTO saveSubmission(SubmissionDTO submissionDTO) {
        logger.info("Saving submission");
        validateSubmissionDTO(submissionDTO);
        Submission submission = submissionMapper.toSubmission(submissionDTO);
        Submission savedSubmission = submissionRepository.save(submission);
        logger.info("Submission saved with ID: {}", savedSubmission.getId());
        return submissionMapper.toSubmissionDTO(savedSubmission);
    }

    /**
     * Deletes a submission by ID.
     *
     * @param id the ID of the submission to delete
     * @throws ResourceNotFoundException if the submission is not found
     */
    @Transactional
    public void deleteSubmission(UUID id) {
        logger.info("Deleting submission with ID: {}", id);
        if (!submissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Submission not found with ID: " + id);
        }
        submissionRepository.deleteById(id);
        logger.info("Submission deleted with ID: {}", id);
    }

    /**
     * Retrieves a submission by ID.
     *
     * @param id the ID of the submission to retrieve
     * @return the submission DTO
     * @throws ResourceNotFoundException if the submission is not found
     */
    public SubmissionDTO getSubmissionById(UUID id) {
        logger.info("Retrieving submission with ID: {}", id);
        Submission submission = findSubmissionById(id);
        return submissionMapper.toSubmissionDTO(submission);
    }

    /**
     * Retrieves all submissions.
     *
     * @return a list of submission DTOs
     */
    public List<SubmissionDTO> getAllSubmissions() {
        logger.info("Retrieving all submissions");
        return submissionRepository.findAll().stream()
                .map(submissionMapper::toSubmissionDTO)
                .toList();
    }

    /**
     * Retrieves submissions by student ID.
     *
     * @param studentId the ID of the student
     * @return a list of submission DTOs
     */
    public List<SubmissionDTO> getSubmissionsByStudentId(UUID studentId) {
        logger.info("Retrieving submissions for student ID: {}", studentId);
        return submissionRepository.findByStudentId(studentId).stream()
                .map(submissionMapper::toSubmissionDTO)
                .toList();
    }

    /**
     * Updates an existing submission.
     *
     * @param id            the ID of the submission to update
     * @param submissionDTO the submission data transfer object
     * @throws ResourceNotFoundException if the submission is not found
     * @throws IllegalArgumentException  if submissionDTO is null
     */
    @Transactional
    public void updateSubmission(UUID id, SubmissionDTO submissionDTO) {
        logger.info("Updating submission with ID: {}", id);
        validateSubmissionDTO(submissionDTO);
        Submission existingSubmission = findSubmissionById(id);
        submissionMapper.updateSubmissionFromDTO(submissionDTO, existingSubmission);
        Submission updatedSubmission = submissionRepository.save(existingSubmission);
        logger.info("Submission updated with ID: {}", updatedSubmission.getId());
        submissionMapper.toSubmissionDTO(updatedSubmission);
    }

    /**
     * Creates a new submission.
     *
     * @param submissionDTO the submission data transfer object
     * @throws IllegalArgumentException if submissionDTO is null
     */
    @Transactional
    public void createSubmission(SubmissionDTO submissionDTO) {
        logger.info("Creating a new submission");
        validateSubmissionDTO(submissionDTO);
        Submission submission = submissionMapper.toSubmission(submissionDTO);
        Submission savedSubmission = submissionRepository.save(submission);
        logger.info("Submission created with ID: {}", savedSubmission.getId());
        submissionMapper.toSubmissionDTO(savedSubmission);
    }

    /**
     * Gets paginated submissions.
     */
    public Page<SubmissionDTO> getSubmissionsPage(Pageable pageable) {
        logger.info("Retrieving submissions page {} of size {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return submissionRepository.findAll(pageable)
                .map(submissionMapper::toSubmissionDTO);
    }

    /**
     * Validates and processes a submission.
     */
    @Transactional
    public SubmissionDTO processSubmission(SubmissionDTO submissionDTO) {
        validateSubmissionDTO(submissionDTO);

        // Add status and timestamp
        return self.saveSubmission(submissionDTO);
    }

    private void validateSubmissionDTO(SubmissionDTO submissionDTO) {
        if (submissionDTO == null) {
            throw new IllegalArgumentException("SubmissionDTO cannot be null");
        }
        // Additional validation logic can be added here
    }

    private Submission findSubmissionById(UUID id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with ID: " + id));
    }
}