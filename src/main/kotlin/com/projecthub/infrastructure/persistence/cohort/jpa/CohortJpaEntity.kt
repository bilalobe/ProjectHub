package com.projecthub.infrastructure.persistence.cohort.jpa

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * JPA entity for persisting Cohorts
 */
@Entity
@Table(name = "cohorts")
class CohortJpaEntity(
    @Id
    var id: String,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, unique = true)
    var code: String,

    @Column(name = "school_id", nullable = false)
    var schoolId: String,

    @Column(name = "max_students", nullable = false)
    var maxStudents: Int,

    @Column(name = "max_teams", nullable = false)
    var maxTeams: Int,

    @Column(name = "start_term", nullable = false)
    var startTerm: LocalDate,

    @Column(name = "end_term", nullable = false)
    var endTerm: LocalDate,

    @Column(nullable = false)
    var isArchived: Boolean = false,

    @Column(length = 500)
    var archiveReason: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    // No-arg constructor required by JPA
    constructor() : this(
        id = "",
        name = "",
        code = "",
        schoolId = "",
        maxStudents = 0,
        maxTeams = 0,
        startTerm = LocalDate.now(),
        endTerm = LocalDate.now().plusMonths(6),
        isArchived = false,
        archiveReason = null,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
}
