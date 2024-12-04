package com.projecthub.repository.jpa;

import com.projecthub.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link Team} entities.
 */
@Repository("jpaTeamRepository")
@Profile("jpa")
public interface TeamJpaRepository extends JpaRepository<Team, UUID> {

    /**
     * Finds a team by its ID.
     *
     * @param teamId the UUID of the team
     * @return an {@code Optional} containing the team if found
     */
    Optional<Team> findTeamById(UUID teamId);

    /**
     * Finds teams by class ID.
     *
     * @param classId the UUID of the class
     * @return a list of {@code Team} objects belonging to the class
     */
    List<Team> findByClassId(UUID classId);

    /**
     * Finds teams by cohort ID.
     *
     * @param cohortId the UUID of the cohort
     * @return a list of {@code Team} objects belonging to the cohort
     */
    List<Team> findByCohortId(UUID cohortId);
}