package com.projecthub.base.student.api.controller;

import com.projecthub.base.shared.domain.enums.sync.SubmissionStatus;
import com.projecthub.base.student.api.dto.SubmissionApi;
import com.projecthub.base.student.api.dto.SubmissionDTO;
import com.projecthub.base.student.application.service.SubmissionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/submissions")
public class SubmissionController implements SubmissionApi {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionController.class);

    private final SubmissionService submissionService;

    public SubmissionController(final SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    public static ResponseEntity<List<SubmissionDTO>> getAllSubmissions() { // No changes needed here
        // Implementation
        return null;
    }

    @Override
    public ResponseEntity<SubmissionDTO> getById(final UUID id) { // No changes needed here
        // Implementation
        return null;
    }


    @Override
    public ResponseEntity<SubmissionDTO> submitWork(@Valid @RequestBody final SubmissionDTO submission) {
        // Ensure timestamp, status, and submittedAt are set here or clarify responsibility in SubmissionService
        submission.setTimestamp(LocalDateTime.now()); // Example, consider timezone handling
        submission.setStatus(SubmissionStatus.SUBMITTED); // Example status
        submission.setSubmittedAt(LocalDateTime.now());
        return ResponseEntity.ok(this.submissionService.submitWork(submission));
    }


    @Override
    public ResponseEntity<SubmissionDTO> update(final UUID id, @Valid @RequestBody final SubmissionDTO submissionDTO) {
        // Check what fields are actually set in submissionDTO
        // Call setTimestamp, setStatus, and setSubmittedAt here if needed (similar as in submitWork)
        // Call correct method to update submission in service
        return null;
    }


    @Override
    public ResponseEntity<SubmissionDTO> create(@Valid @RequestBody final SubmissionDTO submissionDTO) {
        // Check what fields are actually set in submissionDTO
        // Call setTimestamp, setStatus, and setSubmittedAt here if needed (similar as in submitWork)
        // Call correct method to create submission in service
        return null;

    }


    @Override
    public ResponseEntity<Void> deleteById(final UUID id) { // No changes needed here
        // Implementation
        return null;
    }


    @Override
    public ResponseEntity<List<SubmissionDTO>> getSubmissionsByProject(final UUID projectId) {
        // Implementation
        return null;
    }


    @Override
    public ResponseEntity<SubmissionDTO> gradeSubmission(@PathVariable final UUID submissionId, @RequestBody final Integer grade) {
        // Implementation
        return null;
    }
}
