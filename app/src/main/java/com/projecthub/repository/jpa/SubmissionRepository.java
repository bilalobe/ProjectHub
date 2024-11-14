package com.projecthub.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projecthub.model.Submission;
import com.projecthub.repository.custom.CustomSubmissionRepository;

@Repository("postgresSubmissionRepository")
public interface SubmissionRepository extends JpaRepository<Submission, Long>, CustomSubmissionRepository {
    // Custom query methods can be added here
}