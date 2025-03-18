package com.projecthub.infrastructure.persistence.cohort.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * JPA repository for Cohort entities
 */
@Repository
interface CohortJpaRepository : JpaRepository<CohortJpaEntity, String> {

    fun findBySchoolId(schoolId: String): List<CohortJpaEntity>

    @Query(
        "SELECT c FROM CohortJpaEntity c WHERE c.isArchived = false " +
            "AND c.startTerm <= :today AND c.endTerm >= :today " +
            "AND (c.maxStudents - (SELECT COUNT(sa) FROM StudentAssignmentJpaEntity sa WHERE sa.cohortId = c.id)) >= :minCapacity"
    )
    fun findActiveCohortsWithCapacity(
        @Param("today") today: LocalDate = LocalDate.now(),
        @Param("minCapacity") minCapacity: Int
    ): List<CohortJpaEntity>

    @Query(
        "SELECT c FROM CohortJpaEntity c WHERE c.isArchived = false " +
            "AND c.startTerm <= :today AND c.endTerm >= :today"
    )
    fun findActiveCohorts(@Param("today") today: LocalDate = LocalDate.now()): List<CohortJpaEntity>
}
