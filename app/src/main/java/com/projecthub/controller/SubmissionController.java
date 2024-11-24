package com.projecthub.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projecthub.service.SubmissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;

import com.projecthub.dto.SubmissionSummary;
import com.projecthub.exception.ResourceNotFoundException;

@Tag(name = "Submission API", description = "Operations pertaining to submissions in ProjectHub")
@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    private final ModelMapper modelMapper = new ModelMapper();

    @GetMapping
    public ResponseEntity<List<SubmissionSummary>> getAllSubmissions() {
        List<SubmissionSummary> submissions = submissionService.getAllSubmissions().stream()
                .map(submission -> modelMapper.map(submission, SubmissionSummary.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(submissions);
    }

    @Operation(summary = "Create a new submission")
    @PostMapping
    public ResponseEntity<String> createSubmission(@Valid @RequestBody SubmissionSummary submissionSummary) {
        try {
            submissionService.saveSubmission(submissionSummary);
            return ResponseEntity.ok("Submission created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error creating submission");
        }
    }

    @Operation(summary = "Delete a submission")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSubmission(@PathVariable Long id) {
        try {
            submissionService.deleteSubmission(id);
            return ResponseEntity.ok("Submission deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("Submission not found");
        }
    }
}