package com.projecthub.repository.jpa;

import com.projecthub.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link Student} entities.
 */
@Repository("jpaStudentRepository")
@Profile("jpa")
public interface StudentJpaRepository extends JpaRepository<Student, UUID> {

    /**
     * Finds a student by username.
     *
     * @param username the username of the student
     * @return an {@code Optional} containing the student if found
     */
    Optional<Student> findByUsername(String username);

    /**
     * Finds students by team ID.
     *
     * @param teamId the UUID of the team
     * @return a list of {@code Student} objects belonging to the team
     */
    List<Student> findByTeamId(UUID teamId);
}