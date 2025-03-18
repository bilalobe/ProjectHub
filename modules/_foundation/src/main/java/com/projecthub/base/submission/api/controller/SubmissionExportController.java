package com.projecthub.base.submission.api.controller;

import com.projecthub.base.submission.application.service.SubmissionExportService;
import com.projecthub.base.submission.application.service.SubmissionQueryService;
import com.projecthub.base.submission.domain.entity.Submission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NonNls;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/submissions/export")
@Tag(name = "Submission Exports", description = "Export endpoints for submissions")
@RequiredArgsConstructor
public class SubmissionExportController {
    private final SubmissionExportService exportService;
    private final SubmissionQueryService queryService;

    @GetMapping(value = "/project/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Export project submissions as JSON")
    public ResponseEntity<byte[]> exportProjectSubmissionsAsJson(@PathVariable UUID projectId) throws java.io.IOException {
        List<Submission> submissions = queryService.findByProjectId(projectId);
        byte[] data = exportService.exportToJson(submissions);

        @NonNls @NonNls String filename = String.format("submissions-%s-%s.json",
            projectId,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        );

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.APPLICATION_JSON)
            .body(data);
    }

    @GetMapping(value = "/project/{projectId}/csv", produces = "text/csv")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Export project submissions as CSV")
    public ResponseEntity<byte[]> exportProjectSubmissionsAsCsv(@PathVariable UUID projectId) throws java.io.IOException {
        List<Submission> submissions = queryService.findByProjectId(projectId);
        byte[] data = exportService.exportToCsv(submissions);

        @NonNls @NonNls String filename = String.format("submissions-%s-%s.csv",
            projectId,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        );

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(data);
    }

    @GetMapping(value = "/project/{projectId}/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Export project submissions as Excel")
    public ResponseEntity<byte[]> exportProjectSubmissionsAsExcel(@PathVariable UUID projectId) throws java.io.IOException {
        List<Submission> submissions = queryService.findByProjectId(projectId);
        byte[] data = exportService.exportToExcel(submissions);

        @NonNls @NonNls String filename = String.format("submissions-%s-%s.xlsx",
            projectId,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        );

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(data);
    }

    @GetMapping(value = "/submission/{submissionId}/comments/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Export submission comments as Excel")
    public ResponseEntity<byte[]> exportSubmissionComments(@PathVariable UUID submissionId) throws java.io.IOException {
        byte[] data = exportService.exportComments(submissionId);

        @NonNls @NonNls String filename = String.format("submission-comments-%s-%s.xlsx",
            submissionId,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        );

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(data);
    }

    @GetMapping(value = "/student/{studentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'STUDENT')")
    @Operation(summary = "Export student submissions as JSON")
    public ResponseEntity<byte[]> exportStudentSubmissionsAsJson(@PathVariable UUID studentId) throws java.io.IOException {
        List<Submission> submissions = queryService.findByStudentId(studentId);
        byte[] data = exportService.exportToJson(submissions);

        @NonNls @NonNls String filename = String.format("student-submissions-%s-%s.json",
            studentId,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        );

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.APPLICATION_JSON)
            .body(data);
    }

    @GetMapping(value = "/student/{studentId}/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'STUDENT')")
    @Operation(summary = "Export student submissions as Excel")
    public ResponseEntity<byte[]> exportStudentSubmissionsAsExcel(@PathVariable UUID studentId) throws java.io.IOException {
        List<Submission> submissions = queryService.findByStudentId(studentId);
        byte[] data = exportService.exportToExcel(submissions);

        @NonNls @NonNls String filename = String.format("student-submissions-%s-%s.xlsx",
            studentId,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        );

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(data);
    }
}
