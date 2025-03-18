package com.projecthub.base.submission.application.port;

import com.projecthub.base.submission.domain.entity.Submission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubmissionPort {
    Submission save(Submission submission);
    Optional<Submission> findById(UUID id);
    List<Submission> findByStudentId(UUID studentId);
    List<Submission> findByProjectId(UUID projectId);
    List<Submission> findPendingGradingByProjectId(UUID projectId);
    boolean hasValidSubmissionForProject(UUID studentId, UUID projectId);
    void delete(UUID id);
}