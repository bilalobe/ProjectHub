package com.projecthub.core.api;

import com.projecthub.core.dto.SubmissionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@Tag(name = "Submissions", description = "Submission management API")
@RequestMapping("/api/v1/submissions")
public interface SubmissionApi extends BaseApi<SubmissionDTO, UUID> {

    @Operation(summary = "Get submissions by project")
    @GetMapping("/project/{projectId}")
    ResponseEntity<List<SubmissionDTO>> getSubmissionsByProject(@PathVariable UUID projectId);

    @Operation(summary = "Submit work")
    @PostMapping
    ResponseEntity<SubmissionDTO> submitWork(@Valid @RequestBody SubmissionDTO submission);

    @Operation(summary = "Grade submission")
    @PatchMapping("/{submissionId}/grade")
    ResponseEntity<SubmissionDTO> gradeSubmission(
            @PathVariable UUID submissionId,
            @RequestBody Integer grade);
}
