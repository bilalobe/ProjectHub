package com.projecthub.base.student.application.service;


import com.projecthub.base.shared.exception.ResourceNotFoundException;
import com.projecthub.base.student.api.dto.SubmissionDTO;
import com.projecthub.base.student.domain.entity.Submission;
import com.projecthub.base.student.domain.repository.SubmissionJpaRepository;
import com.projecthub.base.submission.api.SubmissionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public SubmissionService(final SubmissionJpaRepository submissionRepository, final SubmissionMapper submissionMapper) {
        this.submissionRepository = submissionRepository;
        this.submissionMapper = submissionMapper;
    }

    /**
     * Saves a new submission.
     *
     * @param submissionDTO the submission data transfer object
     * @return the saved submission DTO
     * @throws IllegalArgumentException if submissionDTO is null
     */
    @Transactional
    public SubmissionDTO saveSubmission(final SubmissionDTO submissionDTO) {
        SubmissionService.logger.info("Saving submission");
        this.validateSubmissionDTO(submissionDTO);
        final Submission submission = this.submissionMapper.toEntity(submissionDTO);
        final Submission savedSubmission = this.submissionRepository.save(submission);
        SubmissionService.logger.info("Submission saved with ID: {}", savedSubmission.getId());
        return this.submissionMapper.toDto(savedSubmission);
    }

    /**
     * Deletes a submission by ID.
     *
     * @param id the ID of the submission to delete
     * @throws ResourceNotFoundException if the submission is not found
     */
    @Transactional
    public void deleteSubmission(final UUID id) {
        SubmissionService.logger.info("Deleting submission with ID: {}", id);
        if (!this.submissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Submission not found with ID: " + id);
        }
        this.submissionRepository.deleteById(id);
        SubmissionService.logger.info("Submission deleted with ID: {}", id);
    }

    /**
     * Retrieves a submission by ID.
     *
     * @param id the ID of the submission to retrieve
     * @return the submission DTO
     * @throws ResourceNotFoundException if the submission is not found
     */
    public SubmissionDTO getSubmissionById(final UUID id) {
        SubmissionService.logger.info("Retrieving submission with ID: {}", id);
        final Submission submission = this.findSubmissionById(id);
        return this.submissionMapper.toDto(submission);
    }

    /**
     * Retrieves all submissions.
     *
     * @return a list of submission DTOs
     */
    public List<SubmissionDTO> getAllSubmissions() {
        SubmissionService.logger.info("Retrieving all submissions");
        return this.submissionRepository.findAll().stream()
            .map(this.submissionMapper::toDto)
            .toList();
    }

    /**
     * Retrieves submissions by student ID.
     *
     * @param studentId the ID of the student
     * @return a list of submission DTOs
     */
    public List<SubmissionDTO> getSubmissionsByStudentId(final UUID studentId) {
        SubmissionService.logger.info("Retrieving submissions for student ID: {}", studentId);
        return this.submissionRepository.findByStudentId(studentId).stream()
            .map(this.submissionMapper::toDto)
            .toList();
    }

    /**
     * Updates an existing submission.
     *
     * @param id            the ID of the submission to update
     * @param submissionDTO the submission data transfer object
     * @return the updated submission DTO
     * @throws ResourceNotFoundException if the submission is not found
     * @throws IllegalArgumentException  if submissionDTO is null
     */
    @Transactional
    public SubmissionDTO updateSubmission(final UUID id, final SubmissionDTO submissionDTO) {
        SubmissionService.logger.info("Updating submission with ID: {}", id);
        this.validateSubmissionDTO(submissionDTO);
        final Submission existingSubmission = this.findSubmissionById(id);
        this.submissionMapper.updateEntityFromDto(submissionDTO, existingSubmission);
        final Submission updatedSubmission = this.submissionRepository.save(existingSubmission);
        SubmissionService.logger.info("Submission updated with ID: {}", updatedSubmission.getId());
        return this.submissionMapper.toDto(updatedSubmission);
    }

    /**
     * Creates a new submission.
     *
     * @param submissionDTO the submission data transfer object
     * @return the created submission DTO
     * @throws IllegalArgumentException if submissionDTO is null
     */
    @Transactional
    public SubmissionDTO createSubmission(final SubmissionDTO submissionDTO) {
        SubmissionService.logger.info("Creating a new submission");
        this.validateSubmissionDTO(submissionDTO);
        final Submission submission = this.submissionMapper.toEntity(submissionDTO);
        final Submission savedSubmission = this.submissionRepository.save(submission);
        SubmissionService.logger.info("Submission created with ID: {}", savedSubmission.getId());
        return this.submissionMapper.toDto(savedSubmission);
    }

    /**
     * Gets paginated submissions.
     *
     * @param pageable the pagination information
     * @return a page of submission DTOs
     */
    public Page<SubmissionDTO> getSubmissionsPage(final Pageable pageable) {
        SubmissionService.logger.info("Retrieving submissions page {} of size {}",
            pageable.getPageNumber(), pageable.getPageSize());
        return this.submissionRepository.findAll(pageable)
            .map(this.submissionMapper::toDto);
    }

    /**
     * Validates and processes a submission.
     *
     * @param submissionDTO the submission data transfer object
     * @return the processed submission DTO
     */
    @Transactional
    public SubmissionDTO processSubmission(final SubmissionDTO submissionDTO) {
        this.validateSubmissionDTO(submissionDTO);

        // Add status and timestamp
        return this.saveSubmission(submissionDTO);
    }

    private void validateSubmissionDTO(final SubmissionDTO submissionDTO) {
        if (null == submissionDTO) {
            throw new IllegalArgumentException("SubmissionDTO cannot be null");
        }
        // Additional validation logic can be added here
    }

    private Submission findSubmissionById(final UUID id) {
        return this.submissionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Submission not found with ID: " + id));
    }
}
