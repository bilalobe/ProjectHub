package com.projecthub.core.repositories.jpa;

import com.projecthub.core.models.Team;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for {@link Team} entities.
 */
@Repository("jpaTeamRepository")
@Profile("jpa")
public interface TeamJpaRepository extends JpaRepository<Team, UUID> {

    /**
     * Finds teams by cohort ID.
     *
     * @param cohortId the UUID of the cohort
     * @return a list of {@code Team} objects belonging to the cohort
     */
    List<Team> findByCohortId(UUID cohortId);
}