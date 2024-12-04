package com.projecthub.repository.csv;

import com.projecthub.model.Student;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for {@link Student} entities.
 */
public interface StudentCsvRepository extends BaseCsvRepository<Student> {

    /**
     * Finds students by team ID.
     *
     * @param teamId the ID of the team
     * @return a list of {@code Student} objects belonging to the team
     */
    List<Student> findByTeamId(UUID teamId);
}