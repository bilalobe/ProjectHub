package com.projecthub.base.submission.api.controller;

import com.projecthub.base.submission.api.dto.BatchGradeRequest;
import com.projecthub.base.submission.api.dto.BatchRevokeRequest;
import com.projecthub.base.submission.api.dto.SubmissionResponse;
import com.projecthub.base.submission.api.mapper.SubmissionMapper;
import com.projecthub.base.submission.application.service.SubmissionBatchService;
import com.projecthub.base.submission.domain.enums.SubmissionStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/submissions/batch")
@Tag(name = "Submission Batch Operations", description = "Batch operations for submissions")
@RequiredArgsConstructor
public class SubmissionBatchController {
    private final SubmissionBatchService batchService;
    private final SubmissionMapper mapper;

    @PostMapping("/grade")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Batch grade multiple submissions")
    public CompletableFuture<ResponseEntity<List<SubmissionResponse>>> batchGrade(
            @Valid @RequestBody BatchGradeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        batchService.validateBatchOperation(request.submissionIds());

        return batchService.batchGrade(
                request.submissionIds(),
                request.grade(),
                request.feedback(),
                UUID.fromString(userDetails.getUsername())
            )
            .thenApply(submissions -> ResponseEntity.ok(
                submissions.stream()
                    .map(mapper::toResponse)
                    .toList()
            ));
    }

    @PostMapping("/revoke")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Batch revoke multiple submissions")
    public CompletableFuture<ResponseEntity<List<SubmissionResponse>>> batchRevoke(
            @Valid @RequestBody BatchRevokeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        batchService.validateBatchOperation(request.submissionIds());

        return batchService.batchRevoke(
                request.submissionIds(),
                request.reason(),
                UUID.fromString(userDetails.getUsername())
            )
            .thenApply(submissions -> ResponseEntity.ok(
                submissions.stream()
                    .map(mapper::toResponse)
                    .toList()
            ));
    }

    @GetMapping("/project/{projectId}/overdue")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Find overdue submissions for a project")
    public CompletableFuture<ResponseEntity<List<UUID>>> findOverdueSubmissions(
            @PathVariable UUID projectId) {
        return batchService.findOverdueSubmissions(projectId)
            .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/project/{projectId}/ungraded")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Find ungraded submissions for a project")
    public CompletableFuture<ResponseEntity<List<UUID>>> findUngradedSubmissions(
            @PathVariable UUID projectId) {
        return batchService.findUngraded(projectId)
            .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/project/{projectId}/count/{status}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Count submissions by status for a project")
    public CompletableFuture<ResponseEntity<Long>> countByStatus(
            @PathVariable UUID projectId,
            @PathVariable SubmissionStatus status) {
        return batchService.countByStatus(status, projectId)
            .thenApply(ResponseEntity::ok);
    }
}
