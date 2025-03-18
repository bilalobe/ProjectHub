package com.projecthub.domain.cohort

/**
 * Repository interface for cohorts (outbound port)
 * This interface defines the persistence operations available for Cohort entities
 */
interface CohortRepository {
    fun findById(id: String): Cohort?
    fun findAll(): List<Cohort>
    fun findBySchool(schoolId: String): List<Cohort>
    fun save(cohort: Cohort): Cohort
    fun deleteById(id: String)
    fun findActiveCohortsWithCapacity(minCapacity: Int): List<Cohort>
}