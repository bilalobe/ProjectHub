package com.projecthub.core.controllers;

import com.projecthub.core.api.SubmissionApi;
import com.projecthub.core.dto.SubmissionDTO;
import com.projecthub.core.enums.SubmissionStatus;
import com.projecthub.core.services.student.SubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/submissions")
public class SubmissionController implements SubmissionApi {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionController.class);

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    public ResponseEntity<List<SubmissionDTO>> getAllSubmissions() { // No changes needed here
        // Implementation
        return null;
    }

    @Override
    public ResponseEntity<SubmissionDTO> getById(UUID id) { // No changes needed here
        // Implementation
        return null;
    }


    @Override
    public ResponseEntity<SubmissionDTO> submitWork(@Valid @RequestBody SubmissionDTO submission) {
        // Ensure timestamp, status, and submittedAt are set here or clarify responsibility in SubmissionService
        submission.setTimestamp(LocalDateTime.now()); // Example, consider timezone handling
        submission.setStatus(SubmissionStatus.SUBMITTED); // Example status
        submission.setSubmittedAt(LocalDateTime.now());
        return ResponseEntity.ok(submissionService.submitWork(submission));
    }


    @Override
    public ResponseEntity<SubmissionDTO> update(UUID id, @Valid @RequestBody SubmissionDTO submissionDTO) {
        // Check what fields are actually set in submissionDTO
        // Call setTimestamp, setStatus, and setSubmittedAt here if needed (similar as in submitWork)
        // Call correct method to update submission in service
        return null;
    }


    @Override
    public ResponseEntity<SubmissionDTO> create(@Valid @RequestBody SubmissionDTO submissionDTO) {
        // Check what fields are actually set in submissionDTO
        // Call setTimestamp, setStatus, and setSubmittedAt here if needed (similar as in submitWork)
        // Call correct method to create submission in service
        return null;

    }



    @Override
    public ResponseEntity<Void> deleteById(UUID id) { // No changes needed here
        // Implementation
        return null;
    }



    @Override
    public ResponseEntity<List<SubmissionDTO>> getSubmissionsByProject(UUID projectId) {
        // Implementation
        return null;
    }


    @Override
    public ResponseEntity<SubmissionDTO> gradeSubmission(@PathVariable UUID submissionId, @RequestBody Integer grade) {
        // Implementation
        return null;
    }
}