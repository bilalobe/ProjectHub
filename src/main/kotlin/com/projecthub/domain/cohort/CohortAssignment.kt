package com.projecthub.domain.cohort

import com.projecthub.domain.ValueObject
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
data class CohortAssignment(
    @Column(nullable = false)
    val year: Int,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val level: GradeLevel,

    @Column(name = "max_students", nullable = false)
    val maxStudents: Int,

    @Column(name = "max_teams", nullable = false)
    val maxTeams: Int
) : ValueObject

enum class GradeLevel {
    FRESHMAN,
    SOPHOMORE,
    JUNIOR,
    SENIOR,
    GRADUATE,
    POSTGRADUATE
}
