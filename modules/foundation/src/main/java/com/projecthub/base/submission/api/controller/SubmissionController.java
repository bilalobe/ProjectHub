package com.projecthub.base.submission.api.controller;

import com.projecthub.base.submission.api.dto.SubmissionDTO;
import com.projecthub.base.submission.api.mapper.SubmissionMapper;
import com.projecthub.base.submission.api.rest.SubmissionApi;
import com.projecthub.base.submission.application.port.in.CreateSubmissionUseCase;
import com.projecthub.base.submission.application.port.in.DeleteSubmissionUseCase;
import com.projecthub.base.submission.application.port.in.LoadSubmissionUseCase;
import com.projecthub.base.submission.application.port.in.UpdateSubmissionUseCase;
import com.projecthub.base.submission.domain.command.CreateSubmissionCommand;
import com.projecthub.base.submission.domain.command.UpdateSubmissionCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
@Slf4j
public class SubmissionController implements SubmissionApi {

    private final CreateSubmissionUseCase createSubmissionUseCase;
    private final UpdateSubmissionUseCase updateSubmissionUseCase;
    private final DeleteSubmissionUseCase deleteSubmissionUseCase;
    private final LoadSubmissionUseCase loadSubmissionUseCase;
    private final SubmissionMapper submissionMapper;


    @Override
    public ResponseEntity<List<SubmissionDTO>> getAllSubmissions() {
        log.info("Getting all submissions");
        return ResponseEntity.ok(loadSubmissionUseCase.getAllSubmissions());
    }

    @Override
    public ResponseEntity<SubmissionDTO> getSubmissionById(UUID id) {
         log.info("Getting submission with ID: {}", id);
        return ResponseEntity.ok(loadSubmissionUseCase.getSubmissionById(id));
    }


    @Override
    public ResponseEntity<List<SubmissionDTO>> getSubmissionsByProject(UUID projectId) {
         log.info("Getting submissions for project with ID: {}", projectId);
       return ResponseEntity.ok(loadSubmissionUseCase.getSubmissionsByProject(projectId));
    }

    @Override
    public ResponseEntity<Page<SubmissionDTO>> getSubmissionsByProject(UUID projectId, Pageable pageable) {
        log.info("Getting paginated submissions for project: {} using page: {} and size: {}", projectId, pageable.getPageNumber(), pageable.getPageSize());
         return ResponseEntity.ok(loadSubmissionUseCase.getSubmissionsByProject(projectId, pageable));
    }

    @Override
    public ResponseEntity<List<SubmissionDTO>> getSubmissionsByStudent(UUID studentId) {
        log.info("Getting submissions for student with ID: {}", studentId);
        return ResponseEntity.ok(loadSubmissionUseCase.getSubmissionsByStudent(studentId));
    }

     @Override
    public ResponseEntity<Page<SubmissionDTO>> getSubmissionsByStudent(UUID studentId, Pageable pageable) {
       log.info("Getting paginated submissions for student: {} with page: {} and size: {}", studentId, pageable.getPageNumber(), pageable.getPageSize());
       return ResponseEntity.ok(loadSubmissionUseCase.getSubmissionsByStudent(studentId, pageable));
    }
    @Override
    public ResponseEntity<SubmissionDTO> createSubmission(@Valid @RequestBody SubmissionDTO submission) {
        log.info("Creating a new submission: {}", submission);
       CreateSubmissionCommand command = CreateSubmissionCommand.builder()
                .content(submission.content())
                .projectId(submission.projectId())
                .studentId(submission.studentId())
                .initiatorId(getInitiatorId())
                 .filePath(submission.filePath())
              .build();
        return ResponseEntity.ok(createSubmissionUseCase.createSubmission(command));
    }
    @Override
    public ResponseEntity<SubmissionDTO> updateSubmission(UUID id, @Valid @RequestBody SubmissionDTO submission) {
          log.info("Updating submission with ID {}:", id);
        UpdateSubmissionCommand command =  UpdateSubmissionCommand.builder()
                .submissionId(id)
                .content(submission.content())
                .grade(submission.grade())
                .feedback(submission.feedback())
                .reviewerNotes(submission.reviewerNotes())
                .targetStatus(submission.status())
                .filePath(submission.filePath())
                 .initiatorId(getInitiatorId())
                .build();

       return ResponseEntity.ok(updateSubmissionUseCase.updateSubmission(command));
    }

   @Override
    public ResponseEntity<Void> deleteSubmission(@PathVariable UUID id) {
        log.info("Deleting submission with ID {}", id);
        deleteSubmissionUseCase.deleteSubmission(id, getInitiatorId());
        return ResponseEntity.noContent().build();
    }
    private UUID getInitiatorId() {
        // Get user ID from context. For now, we use a dummy id
        return UUID.randomUUID();
    }
}