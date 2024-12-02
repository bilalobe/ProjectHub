package com.projecthub.repository.jpa;

import com.projecthub.model.Submission;
import com.projecthub.repository.SubmissionRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA repository interface for {@link Submission} entities.
 */
@Repository("jpaSubmissionRepository")
@Profile("jpa")
public interface SubmissionJpaRepository extends JpaRepository<Submission, Long>, SubmissionRepository {

    @Override
    List<Submission> findByStudentId(Long studentId);

    @Override
    List<Submission> findByProjectId(Long projectId);
}