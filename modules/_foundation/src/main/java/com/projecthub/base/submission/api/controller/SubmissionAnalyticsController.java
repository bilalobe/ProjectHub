package com.projecthub.base.submission.api.controller;

import com.projecthub.base.submission.application.service.SubmissionAnalyticsService;
import com.projecthub.base.submission.application.service.SubmissionAnalyticsService.GradingTimeStats;
import com.projecthub.base.submission.application.service.SubmissionAnalyticsService.ProjectSubmissionStats;
import com.projecthub.base.submission.application.service.SubmissionAnalyticsService.StudentSubmissionStats;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/submissions/analytics")
@Tag(name = "Submission Analytics", description = "Analytics endpoints for submissions")
@RequiredArgsConstructor
public class SubmissionAnalyticsController {
    private final SubmissionAnalyticsService analyticsService;

    @GetMapping("/project/{projectId}/stats")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Get submission statistics for a project")
    public ResponseEntity<ProjectSubmissionStats> getProjectStats(@PathVariable UUID projectId) {
        return ResponseEntity.ok(analyticsService.getProjectStats(projectId));
    }

    @GetMapping("/student/{studentId}/stats")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'STUDENT')")
    @Operation(summary = "Get submission statistics for a student")
    public ResponseEntity<StudentSubmissionStats> getStudentStats(@PathVariable UUID studentId) {
        return ResponseEntity.ok(analyticsService.getStudentStats(studentId));
    }

    @GetMapping("/project/{projectId}/grading-time")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Get grading time statistics for a project")
    public ResponseEntity<GradingTimeStats> getGradingTimeStats(@PathVariable UUID projectId) {
        return ResponseEntity.ok(analyticsService.getGradingTimeStats(projectId));
    }
}
