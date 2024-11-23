package com.projecthub.repository.custom;

import java.util.List;
import java.util.Optional;

import com.projecthub.model.Submission;

public interface CustomSubmissionRepository {
    List<Submission> findAll();
    Submission save(Submission submission);
    void deleteById(Long submissionId);
    List<Submission> findByStudentId(Long studentId);
    Optional<Submission> findById(Long id);
}