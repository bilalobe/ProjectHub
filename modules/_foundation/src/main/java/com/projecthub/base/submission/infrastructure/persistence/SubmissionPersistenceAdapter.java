package com.projecthub.base.submission.infrastructure.persistence;

import com.projecthub.base.submission.application.port.SubmissionPort;
import com.projecthub.base.submission.domain.entity.Submission;
import com.projecthub.base.submission.domain.repository.SubmissionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubmissionPersistenceAdapter implements SubmissionPort {
    private final SubmissionJpaRepository repository;

    @Override
    public Submission save(Submission submission) {
        return repository.save(submission);
    }

    @Override
    public Optional<Submission> findById(UUID id) {
        return repository.findBySubmissionId(id);
    }

    @Override
    public List<Submission> findByStudentId(UUID studentId) {
        return repository.findByStudentIdOrderBySubmittedAtDesc(studentId);
    }

    @Override
    public List<Submission> findByProjectId(UUID projectId) {
        return repository.findByProjectIdOrderBySubmittedAtDesc(projectId);
    }

    @Override
    public List<Submission> findPendingGradingByProjectId(UUID projectId) {
        return repository.findPendingGradingByProjectId(projectId);
    }

    @Override
    public boolean hasValidSubmissionForProject(UUID studentId, UUID projectId) {
        return repository.hasValidSubmissionForProject(studentId, projectId);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}