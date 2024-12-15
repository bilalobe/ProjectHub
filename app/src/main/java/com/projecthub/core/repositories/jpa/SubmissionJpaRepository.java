package com.projecthub.core.repositories.jpa;

import com.projecthub.core.models.Submission;
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
public interface SubmissionJpaRepository extends JpaRepository<Submission, UUID> {

    /**
     * Finds submissions by student ID.
     *
     * @param studentId the UUID of the student
     * @return a list of {@code Submission} objects belonging to the student
     */
    List<Submission> findByStudentId(UUID studentId);

    /**
     * Finds submissions by project ID.
     *
     * @param projectId the UUID of the project
     * @return a list of {@code Submission} objects belonging to the project
     */
    List<Submission> findByProjectId(UUID projectId);
}