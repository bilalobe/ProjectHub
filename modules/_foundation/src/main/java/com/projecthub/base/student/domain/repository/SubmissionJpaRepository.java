package com.projecthub.base.student.domain.repository;

import com.projecthub.base.student.domain.entity.Submission;
import com.projecthub.base.shared.repository.common.BaseRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for {@link Submission} entities.
 */
@Repository("submissionRepository")
@Profile("jpa")
public interface SubmissionJpaRepository extends BaseRepository<Submission, UUID> {
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
