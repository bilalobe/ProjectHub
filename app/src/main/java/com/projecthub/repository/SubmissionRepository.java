package com.projecthub.repository;

import com.projecthub.model.Submission;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Submission} entities.
 */
public interface SubmissionRepository {
    Submission save(Submission submission);
    List<Submission> findAll();
    Optional<Submission> findById(Long id);
    void delete(Submission submission);
    List<Submission> findByStudentId(Long studentId);
    List<Submission> findByProjectId(Long projectId);
}