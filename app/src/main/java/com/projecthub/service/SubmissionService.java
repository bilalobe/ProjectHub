package com.projecthub.service;

import com.projecthub.dto.SubmissionDTO;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.mapper.SubmissionMapper;
import com.projecthub.model.Submission;
import com.projecthub.repository.SubmissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing submissions.
 */
@Service
public class SubmissionService {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);

    private final SubmissionRepository submissionRepository;
    private final SubmissionMapper submissionMapper;

    public SubmissionService(SubmissionRepository submissionRepository, SubmissionMapper submissionMapper) {
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
    public SubmissionDTO saveSubmission(SubmissionDTO submissionDTO) {
        logger.info("Saving... submission");
        validateSubmissionDTO(submissionDTO);
        Submission submission = submissionMapper.toSubmission(submissionDTO);
        Submission savedSubmission = submissionRepository.save(submission);
        logger.info("Submission created with ID: {}", savedSubmission.getId());
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
                .collect(Collectors.toList());
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
                .collect(Collectors.toList());
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
    public SubmissionDTO updateSubmission(UUID id, SubmissionDTO submissionDTO) {
        logger.info("Updating submission with ID: {}", id);
        validateSubmissionDTO(submissionDTO);
        Submission existingSubmission = findSubmissionById(id);
        submissionMapper.updateSubmissionFromDTO(submissionDTO, existingSubmission);
        Submission updatedSubmission = submissionRepository.save(existingSubmission);
        logger.info("Submission updated with ID: {}", updatedSubmission.getId());
        return submissionMapper.toSubmissionDTO(updatedSubmission);
    }

    /**
     * Creates a new submission.
     *
     * @param submissionDTO the submission data transfer object
     * @return the created submission DTO
     * @throws IllegalArgumentException if submissionDTO is null
     */
    @Transactional
    public SubmissionDTO createSubmission(SubmissionDTO submissionDTO) {
        logger.info("Creating a new submission");
        validateSubmissionDTO(submissionDTO);
        Submission submission = submissionMapper.toSubmission(submissionDTO);
        Submission savedSubmission = submissionRepository.save(submission);
        logger.info("Submission created with ID: {}", savedSubmission.getId());
        return submissionMapper.toSubmissionDTO(savedSubmission);
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