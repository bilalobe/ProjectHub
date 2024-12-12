package com.projecthub.repository.jpa;

import com.projecthub.model.Submission;
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
     * @param student_id the UUID of the student
     * @return a list of {@code Submission} objects belonging to the student
     */
    <PK> List<Submission> findByStudentId(PK student_id);

    /**
     * Finds submissions by project ID.
     *
     * @param project_id the UUID of the project
     * @return a list of {@code Submission} objects belonging to the project
     */
    <PK> List<Submission> findByProjectId(PK project_id);
}