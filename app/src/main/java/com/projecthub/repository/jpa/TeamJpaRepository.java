package com.projecthub.repository.jpa;

import com.projecthub.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Team} entities.
 */
@Repository("jpaTeamRepository")
@Profile("jpa")
public interface TeamJpaRepository extends JpaRepository<Team, Long> {

    /**
     * Finds a team by its ID.
     *
     * @param teamId the ID of the team
     * @return an {@code Optional} containing the team if found
     */
    Optional<Team> findTeamById(Long teamId);

    /**
     * Finds teams by class ID.
     *
     * @param classId the ID of the class
     * @return a list of {@code Team} objects belonging to the class
     */
    List<Team> findByClassId(Long classId);

    /**
     * Finds teams by cohort ID.
     *
     * @param cohortId the ID of the cohort
     * @return a list of {@code Team} objects belonging to the cohort
     */
    List<Team> findByCohortId(Long cohortId);
}