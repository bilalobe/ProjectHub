package com.projecthub.application.cohort.port.`in`

import com.projecthub.application.cohort.dto.CohortDTO
import com.projecthub.application.cohort.dto.CreateCohortCommand
import com.projecthub.application.cohort.dto.UpdateCohortCommand
import com.projecthub.application.cohort.dto.AssignStudentCommand

/**
 * Inbound port for cohort management functionality
 * This interface defines the use cases available for managing cohorts
 */
interface CohortManagementUseCase {
    /**
     * Creates a new cohort
     */
    fun createCohort(command: CreateCohortCommand): CohortDTO

    /**
     * Updates an existing cohort
     */
    fun updateCohort(cohortId: String, command: UpdateCohortCommand): CohortDTO

    /**
     * Assigns a student to a cohort
     */
    fun assignStudentToCohort(command: AssignStudentCommand): CohortDTO

    /**
     * Completes/graduates a cohort
     */
    fun completeCohort(cohortId: String): CohortDTO

    /**
     * Gets a cohort by ID
     */
    fun getCohortById(cohortId: String): CohortDTO?

    /**
     * Gets all cohorts
     */
    fun getAllCohorts(): List<CohortDTO>

    /**
     * Gets cohorts by school
     */
    fun getCohortsBySchool(schoolId: String): List<CohortDTO>

    /**
     * Gets active cohorts with available capacity
     */
    fun getActiveCohortsWithCapacity(minCapacity: Int): List<CohortDTO>
}
