package com.projecthub.ui.cohort.model

import java.time.LocalDate
import java.time.LocalDateTime

data class CohortState(
    val cohorts: List<Cohort> = emptyList(),
    val selectedCohort: Cohort? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class Cohort(
    val id: String,
    val name: String,
    val term: Term,
    val students: List<StudentInfo> = emptyList(),
    val instructors: List<InstructorInfo> = emptyList(),
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: CohortStatus = CohortStatus.ACTIVE,
    val capacity: Int = 30,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class Term(
    val id: String,
    val name: String,
    val year: Int,
    val type: TermType
)

enum class TermType {
    FALL,
    SPRING,
    SUMMER,
    WINTER
}

data class StudentInfo(
    val id: String,
    val name: String,
    val email: String,
    val enrollmentDate: LocalDate,
    val status: EnrollmentStatus
)

data class InstructorInfo(
    val id: String,
    val name: String,
    val email: String,
    val specialization: String,
    val assignedDate: LocalDate
)

enum class CohortStatus {
    PLANNED,
    ACTIVE,
    COMPLETED,
    CANCELLED
}

enum class EnrollmentStatus {
    ENROLLED,
    ON_LEAVE,
    WITHDRAWN,
    GRADUATED
}

sealed class CohortEvent {
    object LoadCohorts : CohortEvent()
    data class SelectCohort(val cohortId: String) : CohortEvent()
    data class CreateCohort(val cohort: Cohort) : CohortEvent()
    data class UpdateCohort(val cohort: Cohort) : CohortEvent()
    data class UpdateCohortStatus(val cohortId: String, val status: CohortStatus) : CohortEvent()
    data class EnrollStudent(val cohortId: String, val studentId: String) : CohortEvent()
    data class UpdateEnrollmentStatus(val cohortId: String, val studentId: String, val status: EnrollmentStatus) : CohortEvent()
    data class AssignInstructor(val cohortId: String, val instructorId: String) : CohortEvent()
    data class RemoveInstructor(val cohortId: String, val instructorId: String) : CohortEvent()
    object ClearError : CohortEvent()
}

// Type aliases for the student and instructor cards
typealias CohortStudent = StudentInfo
typealias CohortInstructor = InstructorInfo