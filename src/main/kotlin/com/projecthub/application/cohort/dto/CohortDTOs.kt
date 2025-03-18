package com.projecthub.application.cohort.dto

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Data Transfer Object for Cohort entities
 */
data class CohortDTO(
    val id: String,
    val name: String,
    val code: String,
    val schoolId: String,
    val capacity: Int,
    val currentEnrollment: Int,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

/**
 * Command for creating a new cohort
 */
data class CreateCohortCommand(
    val name: String,
    val code: String,
    val schoolId: String,
    val capacity: Int,
    val startDate: LocalDate,
    val endDate: LocalDate? = null
)

/**
 * Command for updating an existing cohort
 */
data class UpdateCohortCommand(
    val name: String,
    val capacity: Int,
    val startDate: LocalDate,
    val endDate: LocalDate?
)

/**
 * Command for assigning a student to a cohort
 */
data class AssignStudentCommand(
    val cohortId: String,
    val studentId: String
)