package com.projecthub.base.submission.application.service;

import com.projecthub.base.milestone.domain.exception.MilestoneNotFoundException;
import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.domain.repository.ProjectRepository;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import com.projecthub.base.submission.api.dto.SubmissionDTO;
import com.projecthub.base.submission.api.mapper.SubmissionMapper;
import com.projecthub.base.submission.application.port.in.CreateSubmissionUseCase;
import com.projecthub.base.submission.application.port.in.DeleteSubmissionUseCase;
import com.projecthub.base.submission.application.port.in.UpdateSubmissionUseCase;
import com.projecthub.base.submission.application.port.out.SubmissionEventPort;
import com.projecthub.base.submission.application.port.out.SubmissionPort;
import com.projecthub.base.submission.domain.command.CreateSubmissionCommand;
import com.projecthub.base.submission.domain.command.DeleteSubmissionCommand;
import com.projecthub.base.submission.domain.command.UpdateSubmissionCommand;
import com.projecthub.base.submission.domain.entity.Submission;
import com.projecthub.base.submission.domain.event.SubmissionEvent;
import com.projecthub.base.submission.domain.validation.SubmissionValidator;
import com.projecthub.base.student.domain.entity.Student;
import com.projecthub.base.student.domain.repository.StudentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import com.projecthub.base.shared.exception.ValidationException;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubmissionCommandService implements CreateSubmissionUseCase, UpdateSubmissionUseCase, DeleteSubmissionUseCase {

    private final SubmissionPort submissionPort;
    private final SubmissionEventPort submissionEventPort;
    private final SubmissionMapper submissionMapper;
    private final SubmissionValidator submissionValidator;
    private final StudentRepository studentRepository;
    private final ProjectRepository projectRepository;
    private final SubmissionQueryService submissionQueryService;

    @Override
    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        timeout = 30,
        rollbackFor = {ValidationException.class, ResourceNotFoundException.class}
    )
    public SubmissionDTO createSubmission(CreateSubmissionCommand command) {
        log.debug("Creating a new submission: {}", command);
        Submission submission = prepareSubmission(command);
        submissionValidator.validateCreate(submission);
        
        Submission saved = saveSubmission(submission);
        publishCreatedEvent(saved, command.getInitiatorId());
        
        log.info("Created submission with ID: {}", saved.getId());
        return submissionMapper.toDto(saved);
    }

    @Override
    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        timeout = 30,
        rollbackFor = {ValidationException.class, ResourceNotFoundException.class}
    )
    public SubmissionDTO updateSubmission(UpdateSubmissionCommand command) {
        log.debug("Updating submission with ID: {}", command.getSubmissionId());
        
        Submission submission = findSubmissionById(command.getSubmissionId());
        updateSubmissionFields(submission, command);
        submissionValidator.validateUpdate(submission);
        
        Submission updated = saveSubmission(submission);
        publishUpdatedEvent(updated, command.getInitiatorId());
        
        log.info("Updated submission with ID: {}", updated.getId());
        return submissionMapper.toDto(updated);
    }

    @Override
    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        timeout = 30,
        rollbackFor = {ValidationException.class, ResourceNotFoundException.class}
    )
    public void deleteSubmission(UUID id, UUID initiatorId) {
        log.debug("Deleting submission with ID: {}", id);
        
        Submission submission = findSubmissionById(id);
        submissionValidator.validateDelete(submission);
        
        deleteSubmissionById(id);
        publishDeletedEvent(submission, initiatorId);
        
        log.info("Submission with ID {} deleted.", id);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    protected Project getProjectById(UUID projectId) {
        return projectRepository.findById(projectId)
            .orElseThrow(() -> new MilestoneNotFoundException("Project not found with id " + projectId));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    protected Student getStudentById(UUID studentId) {
        return studentRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    protected Submission findSubmissionById(UUID id) {
        return submissionPort.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + id));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void publishCreatedEvent(Submission submission, UUID initiatorId) {
        submissionEventPort.publish(new SubmissionEvent.SubmissionCreated(
            UUID.randomUUID(),
            submission.getId(),
            initiatorId,
            Instant.now()
        ));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void publishUpdatedEvent(Submission submission, UUID initiatorId) {
        submissionEventPort.publish(new SubmissionEvent.SubmissionUpdated(
            UUID.randomUUID(),
            submission.getId(),
            initiatorId,
            Instant.now()
        ));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void publishDeletedEvent(Submission submission, UUID initiatorId) {
        submissionEventPort.publish(new SubmissionEvent.SubmissionDeleted(
            UUID.randomUUID(),
            submission.getId(),
            initiatorId,
            Instant.now()
        ));
    }

    private Submission prepareSubmission(CreateSubmissionCommand command) {
        Submission submission = submissionMapper.toEntity(command);
        submission.setProject(getProjectById(command.getProjectId()));
        submission.setStudent(getStudentById(command.getStudentId()));
        return submission;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    protected Submission saveSubmission(Submission submission) {
        return submissionPort.save(submission);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    protected void deleteSubmissionById(UUID id) {
        submissionPort.deleteById(id);
    }

    private void updateSubmissionFields(Submission submission, UpdateSubmissionCommand command) {
        submission.setContent(command.getContent());
        if (command.getGrade() != null) {
            submission.grade(command.getGrade(), command.getFeedback());
        }
        if (command.getReviewerNotes() != null) {
            submission.setReviewerNotes(command.getReviewerNotes());
        }
        submission.setFilePath(command.getFilePath());
    }
}