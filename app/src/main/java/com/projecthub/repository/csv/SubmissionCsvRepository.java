package com.projecthub.repository.csv;

import com.projecthub.model.Submission;

import java.util.List;
import java.util.UUID;

// import org.springframework.context.annotation.Profile;
// import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Submission} entities.
 */
// @Repository("submissionCsvRepository")
// @Profile("csv")
public interface SubmissionCsvRepository extends BaseCsvRepository<Submission> {

    /**
     * Finds submissions by student ID.
     *
     * @param studentId the ID of the student
     * @return a list of {@code Submission} objects belonging to the student
     */
    List<Submission> findByStudentId(UUID studentId);
    List<Submission> findByProjectId(UUID projectId);
}