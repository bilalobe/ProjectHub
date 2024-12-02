package com.projecthub.repository.csv;

import com.projecthub.model.Student;
import java.util.List;

/**
 * Repository interface for {@link Student} entities.
 */
public interface StudentCsvRepository extends BaseCsvRepository<Student, Long> {

    /**
     * Finds students by team ID.
     *
     * @param teamId the ID of the team
     * @return a list of {@code Student} objects belonging to the team
     */
    List<Student> findByTeamId(Long teamId);
}