package com.projecthub.core.controllers;

import com.projecthub.core.dto.SubmissionDTO;
import com.projecthub.core.exceptions.ResourceNotFoundException;
import com.projecthub.core.services.student.SubmissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Submission API", description = "Operations pertaining to submissions in ProjectHub")
@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionController.class);

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @GetMapping
    public ResponseEntity<List<SubmissionDTO>> getAllSubmissions() {
        List<SubmissionDTO> submissions = submissionService.getAllSubmissions();
        return ResponseEntity.ok(submissions);
    }

    @Operation(summary = "Create a new submission")
    @PostMapping
    public ResponseEntity<SubmissionDTO> createSubmission(@Valid @RequestBody SubmissionDTO submissionDTO) {
        try {
            SubmissionDTO createdSubmission = submissionService.saveSubmission(submissionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSubmission);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @Operation(summary = "Delete a submission")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSubmission(@PathVariable UUID id) {
        logger.info("Deleting submission with ID {}", id);
        submissionService.deleteSubmission(id);
        return ResponseEntity.ok("Submission deleted successfully");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.error("Resource not found", ex);
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}