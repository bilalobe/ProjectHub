package com.projecthub.domain.cohort.event

import com.projecthub.domain.event.DomainEvent

/**
 * Event fired when a new cohort is created
 */
data class CohortCreatedEvent(
    val cohortId: String,
    val name: String,
    val schoolId: String,
    val capacity: Int
) : DomainEvent()

/**
 * Event fired when a cohort's details are updated
 */
data class CohortUpdatedEvent(
    val cohortId: String,
    val name: String,
    val capacity: Int
) : DomainEvent()

/**
 * Event fired when a student is assigned to a cohort
 */
data class StudentAssignedToCohortEvent(
    val cohortId: String,
    val studentId: String,
    val assignmentDate: String
) : DomainEvent()

/**
 * Event fired when a cohort is completed/graduated
 */
data class CohortCompletedEvent(
    val cohortId: String,
    val completionDate: String
) : DomainEvent()