package com.projecthub.base.cohort.infrastructure.repository;

import com.projecthub.base.cohort.domain.entity.Cohort;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for {@link Cohort} entities.
 */
@Repository("jpaCohortRepository")
@Profile("jpa")
public interface CohortJpaRepository extends JpaRepository<Cohort, UUID> {

    /**
     * Finds cohorts by school ID.
     *
     * @param schoolId the UUID of the school
     * @param pageable
     * @return a list of {@code Cohort} objects belonging to the school
     */
    Page<Cohort> findBySchoolId(UUID schoolId, Pageable pageable);

    @Query("SELECT c FROM Cohort c WHERE c.school.id = :schoolId AND c.assignment.year = :year")
    List<Cohort> findBySchoolIdAndYear(@Param("schoolId") UUID schoolId, @Param("year") String year);

    @Query("SELECT COUNT(c) FROM Cohort c WHERE c.school.id = :schoolId AND c.assignment.year = :year")
    long countBySchoolIdAndYear(@Param("schoolId") UUID schoolId, @Param("year") String year);

    /**
     * Finds all active cohorts with pagination.
     *
     * @param pageable pagination information
     * @return a page of active cohorts
     */
    Page<Cohort> findByIsActiveTrue(Pageable pageable);

    /**
     * Finds all archived cohorts with pagination.
     *
     * @param pageable pagination information
     * @return a page of archived cohorts
     */
    Page<Cohort> findByIsArchivedTrue(Pageable pageable);

    /**
     * Finds all active cohorts for a specific school with pagination.
     *
     * @param schoolId the UUID of the school
     * @param pageable pagination information
     * @return a page of active cohorts for the school
     */
    @Query("SELECT c FROM Cohort c WHERE c.school.id = :schoolId AND c.isActive = true")
    Page<Cohort> findBySchoolIdAndIsActiveTrue(@Param("schoolId") UUID schoolId, Pageable pageable);

    /**
     * Finds all archived cohorts for a specific school with pagination.
     *
     * @param schoolId the UUID of the school
     * @param pageable pagination information
     * @return a page of archived cohorts for the school
     */
    @Query("SELECT c FROM Cohort c WHERE c.school.id = :schoolId AND c.isArchived = true")
    Page<Cohort> findBySchoolIdAndIsArchivedTrue(@Param("schoolId") UUID schoolId, Pageable pageable);
}
