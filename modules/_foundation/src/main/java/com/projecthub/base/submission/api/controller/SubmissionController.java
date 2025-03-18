package com.projecthub.base.submission.api.controller;

import com.projecthub.base.submission.api.dto.CreateSubmissionRequest;
import com.projecthub.base.submission.api.dto.GradeSubmissionRequest;
import com.projecthub.base.submission.api.dto.SubmissionResponse;
import com.projecthub.base.submission.api.dto.UpdateSubmissionRequest;
import com.projecthub.base.submission.api.mapper.SubmissionMapper;
import com.projecthub.base.submission.application.command.SubmissionCommand;
import com.projecthub.base.submission.application.service.SubmissionCommandService;
import com.projecthub.base.submission.application.service.SubmissionQueryService;
import com.projecthub.base.submission.domain.entity.Submission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/submissions")
@Tag(name = "Submission", description = "Submission management APIs")
@RequiredArgsConstructor
public class SubmissionController {
    
    private final SubmissionCommandService commandService;
    private final SubmissionQueryService queryService;
    private final SubmissionMapper mapper;

    @PostMapping
    @Operation(summary = "Create a new submission")
    public ResponseEntity<SubmissionResponse> createSubmission(
            @Valid @RequestBody CreateSubmissionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        SubmissionCommand.CreateSubmission command = new SubmissionCommand.CreateSubmission(
            request.studentId(),
            request.projectId(),
            request.content(),
            request.filePath(),
            request.isLate(),
            UUID.fromString(userDetails.getUsername())
        );

        Submission submission = commandService.handleCreate(command);
        return ResponseEntity.ok(mapper.toResponse(submission));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a submission")
    public ResponseEntity<SubmissionResponse> updateSubmission(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSubmissionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        SubmissionCommand.UpdateSubmission command = new SubmissionCommand.UpdateSubmission(
            id,
            request.content(),
            request.filePath(),
            UUID.fromString(userDetails.getUsername())
        );

        Submission submission = commandService.handleUpdate(command);
        return ResponseEntity.ok(mapper.toResponse(submission));
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "Submit a submission for grading")
    public ResponseEntity<SubmissionResponse> submitSubmission(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        SubmissionCommand.SubmitSubmission command = new SubmissionCommand.SubmitSubmission(
            id,
            UUID.fromString(userDetails.getUsername())
        );

        Submission submission = commandService.handleSubmit(command);
        return ResponseEntity.ok(mapper.toResponse(submission));
    }

    @PostMapping("/{id}/grade")
    @Operation(summary = "Grade a submission")
    public ResponseEntity<SubmissionResponse> gradeSubmission(
            @PathVariable UUID id,
            @Valid @RequestBody GradeSubmissionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        SubmissionCommand.GradeSubmission command = new SubmissionCommand.GradeSubmission(
            id,
            request.grade(),
            request.feedback(),
            UUID.fromString(userDetails.getUsername())
        );

        Submission submission = commandService.handleGrade(command);
        return ResponseEntity.ok(mapper.toResponse(submission));
    }

    @PostMapping("/{id}/revoke")
    @Operation(summary = "Revoke a submission")
    public ResponseEntity<SubmissionResponse> revokeSubmission(
            @PathVariable UUID id,
            @RequestParam String reason,
            @AuthenticationPrincipal UserDetails userDetails) {

        SubmissionCommand.RevokeSubmission command = new SubmissionCommand.RevokeSubmission(
            id,
            reason,
            UUID.fromString(userDetails.getUsername())
        );

        Submission submission = commandService.handleRevoke(command);
        return ResponseEntity.ok(mapper.toResponse(submission));
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Add a comment to a submission")
    public ResponseEntity<SubmissionResponse> addComment(
            @PathVariable UUID id,
            @RequestParam String text,
            @AuthenticationPrincipal UserDetails userDetails) {

        SubmissionCommand.AddComment command = new SubmissionCommand.AddComment(
            id,
            text,
            UUID.fromString(userDetails.getUsername())
        );

        Submission submission = commandService.handleAddComment(command);
        return ResponseEntity.ok(mapper.toResponse(submission));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a submission by ID")
    public ResponseEntity<SubmissionResponse> getSubmission(@PathVariable UUID id) {
        return queryService.findById(id)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get all submissions for a student")
    public ResponseEntity<List<SubmissionResponse>> getStudentSubmissions(@PathVariable UUID studentId) {
        List<SubmissionResponse> submissions = queryService.findByStudentId(studentId)
            .stream()
            .map(mapper::toResponse)
            .toList();
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all submissions for a project")
    public ResponseEntity<List<SubmissionResponse>> getProjectSubmissions(@PathVariable UUID projectId) {
        List<SubmissionResponse> submissions = queryService.findByProjectId(projectId)
            .stream()
            .map(mapper::toResponse)
            .toList();
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/project/{projectId}/pending")
    @Operation(summary = "Get pending submissions for a project")
    public ResponseEntity<List<SubmissionResponse>> getPendingSubmissions(@PathVariable UUID projectId) {
        List<SubmissionResponse> submissions = queryService.findPendingGradingByProjectId(projectId)
            .stream()
            .map(mapper::toResponse)
            .toList();
        return ResponseEntity.ok(submissions);
    }
}
