package com.projecthub.repository.jpa;

import com.projecthub.model.Submission;
import com.projecthub.repository.SubmissionRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA repository interface for {@link Submission} entities.
 */
@Repository("jpaSubmissionRepository")
@Profile("jpa")
public interface SubmissionJpaRepository extends JpaRepository<Submission, UUID>, SubmissionRepository {

    /**
     * Finds submissions by student ID.
     *
     * @param studentId the UUID of the student
     * @return a list of {@code Submission} objects belonging to the student
     */
    @Override
    List<Submission> findByStudentId(UUID studentId);

    /**
     * Finds submissions by project ID.
     *
     * @param projectId the UUID of the project
     * @return a list of {@code Submission} objects belonging to the project
     */
    @Override
    List<Submission> findByProjectId(UUID projectId);
}