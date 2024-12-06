package com.projecthub.repository;

import com.projecthub.model.Submission;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link Submission} entities.
 */
public interface SubmissionRepository {
    Submission save(Submission submission);
    List<Submission> findAll();
    Optional<Submission> findById(UUID id);
    void delete(Submission submission);
    List<Submission> findByStudentId(UUID studentId);
    List<Submission> findByProjectId(UUID projectId);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}