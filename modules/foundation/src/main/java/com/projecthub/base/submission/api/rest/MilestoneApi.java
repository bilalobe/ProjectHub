package com.projecthub.base.submission.api.rest;

import com.projecthub.base.submission.api.dto.SubmissionDTO;
import com.projecthub.core.shared.api.rest.BaseApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Submissions", description = "Submission management API")
@RequestMapping("/api/v1/submissions")
public interface SubmissionApi extends BaseApi<SubmissionDTO, UUID> {

    @Operation(summary = "Get all submissions")
    @GetMapping
    ResponseEntity<List<SubmissionDTO>> getAllSubmissions();

    @Operation(summary = "Get submission by ID")
    @GetMapping("/{id}")
    ResponseEntity<SubmissionDTO> getSubmissionById(@PathVariable UUID id);

     @Operation(summary = "Get submissions by project")
    @GetMapping("/project/{projectId}")
   ResponseEntity<List<SubmissionDTO>> getSubmissionsByProject(@PathVariable UUID projectId);

    @Operation(summary = "Get paginated submissions by project")
    @GetMapping("/project/{projectId}/page")
    ResponseEntity<Page<SubmissionDTO>> getSubmissionsByProject(@PathVariable UUID projectId, Pageable pageable);

   @Operation(summary = "Get submissions by student")
    @GetMapping("/student/{studentId}")
    ResponseEntity<List<SubmissionDTO>> getSubmissionsByStudent(@PathVariable UUID studentId);


   @Operation(summary = "Get paginated submissions by student")
    @GetMapping("/student/{studentId}/page")
   ResponseEntity<Page<SubmissionDTO>> getSubmissionsByStudent(@PathVariable UUID studentId, Pageable pageable);

    @Operation(summary = "Create new submission")
    @PostMapping
    ResponseEntity<SubmissionDTO> createSubmission(@Valid @RequestBody SubmissionDTO submission);

   @Operation(summary = "Update submission")
    @PutMapping("/{id}")
    ResponseEntity<SubmissionDTO> updateSubmission(
        @PathVariable UUID id,
        @Valid @RequestBody SubmissionDTO submission);

    @Operation(summary = "Delete submission")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteSubmission(@PathVariable UUID id);
}