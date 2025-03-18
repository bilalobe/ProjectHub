package com.projecthub.base.submission.application.service;

import com.projecthub.base.shared.exception.ResourceNotFoundException;
import com.projecthub.base.submission.application.command.SubmissionCommand;
import com.projecthub.base.submission.application.port.SubmissionPort;
import com.projecthub.base.submission.domain.aggregate.SubmissionAggregate;
import com.projecthub.base.submission.domain.entity.Submission;
import com.projecthub.base.submission.infrastructure.event.adapter.SubmissionEventAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SubmissionCommandService {
    private final SubmissionPort submissionPort;
    private final SubmissionEventAdapter eventAdapter;

    public Submission handleCreate(SubmissionCommand.CreateSubmission cmd) {
        log.debug("Creating submission for student {} and project {}", cmd.studentId(), cmd.projectId());
        
        SubmissionAggregate aggregate = SubmissionAggregate.create(
            cmd.studentId(),
            cmd.projectId(),
            cmd.content(),
            cmd.filePath(),
            cmd.isLate(),
            cmd.initiatorId()
        );

        Submission saved = submissionPort.save(aggregate.getRoot());
        aggregate.getDomainEvents().forEach(eventAdapter::publish);
        
        log.info("Created submission with ID: {}", saved.getSubmissionId());
        return saved;
    }

    public Submission handleUpdate(SubmissionCommand.UpdateSubmission cmd) {
        log.debug("Updating submission {}", cmd.submissionId());
        
        Submission submission = submissionPort.findById(cmd.submissionId())
            .orElseThrow(() -> new ResourceNotFoundException("Submission not found: " + cmd.submissionId()));

        SubmissionAggregate aggregate = SubmissionAggregate.reconstitute(submission, cmd.initiatorId());
        aggregate.updateContent(cmd.content(), cmd.filePath());

        Submission saved = submissionPort.save(aggregate.getRoot());
        aggregate.getDomainEvents().forEach(eventAdapter::publish);
        
        log.info("Updated submission {}", saved.getSubmissionId());
        return saved;
    }

    public Submission handleSubmit(SubmissionCommand.SubmitSubmission cmd) {
        log.debug("Submitting submission {}", cmd.submissionId());
        
        Submission submission = submissionPort.findById(cmd.submissionId())
            .orElseThrow(() -> new ResourceNotFoundException("Submission not found: " + cmd.submissionId()));

        SubmissionAggregate aggregate = SubmissionAggregate.reconstitute(submission, cmd.initiatorId());
        aggregate.submit();

        Submission saved = submissionPort.save(aggregate.getRoot());
        aggregate.getDomainEvents().forEach(eventAdapter::publish);
        
        log.info("Submitted submission {}", saved.getSubmissionId());
        return saved;
    }

    public Submission handleGrade(SubmissionCommand.GradeSubmission cmd) {
        log.debug("Grading submission {}", cmd.submissionId());
        
        Submission submission = submissionPort.findById(cmd.submissionId())
            .orElseThrow(() -> new ResourceNotFoundException("Submission not found: " + cmd.submissionId()));

        SubmissionAggregate aggregate = SubmissionAggregate.reconstitute(submission, cmd.initiatorId());
        aggregate.grade(cmd.grade(), cmd.feedback());

        Submission saved = submissionPort.save(aggregate.getRoot());
        aggregate.getDomainEvents().forEach(eventAdapter::publish);
        
        log.info("Graded submission {} with grade {}", saved.getSubmissionId(), cmd.grade());
        return saved;
    }

    public Submission handleRevoke(SubmissionCommand.RevokeSubmission cmd) {
        log.debug("Revoking submission {}", cmd.submissionId());
        
        Submission submission = submissionPort.findById(cmd.submissionId())
            .orElseThrow(() -> new ResourceNotFoundException("Submission not found: " + cmd.submissionId()));

        SubmissionAggregate aggregate = SubmissionAggregate.reconstitute(submission, cmd.initiatorId());
        aggregate.revoke(cmd.reason());

        Submission saved = submissionPort.save(aggregate.getRoot());
        aggregate.getDomainEvents().forEach(eventAdapter::publish);
        
        log.info("Revoked submission {}", saved.getSubmissionId());
        return saved;
    }

    public Submission handleAddComment(SubmissionCommand.AddComment cmd) {
        log.debug("Adding comment to submission {}", cmd.submissionId());
        
        Submission submission = submissionPort.findById(cmd.submissionId())
            .orElseThrow(() -> new ResourceNotFoundException("Submission not found: " + cmd.submissionId()));

        SubmissionAggregate aggregate = SubmissionAggregate.reconstitute(submission, cmd.initiatorId());
        aggregate.addComment(cmd.text());

        Submission saved = submissionPort.save(aggregate.getRoot());
        aggregate.getDomainEvents().forEach(eventAdapter::publish);
        
        log.info("Added comment to submission {}", saved.getSubmissionId());
        return saved;
    }
}
