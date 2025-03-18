package com.projecthub.domain.cohort

import com.projecthub.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "schools")
class School(
    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var isActive: Boolean = true,

    @Column(length = 500)
    var description: String? = null
) : BaseEntity() {

    @OneToMany(mappedBy = "school")
    private val _cohorts: MutableSet<Cohort> = mutableSetOf()
    val cohorts: Set<Cohort> get() = _cohorts.toSet()

    fun addCohort(cohort: Cohort) {
        require(isActive) { "Cannot add cohort to inactive school" }

        // Validate cohort year capacity
        val cohortsInYear = _cohorts.count { it.assignment.year == cohort.assignment.year }
        require(cohortsInYear < Cohort.MAX_COHORTS_PER_YEAR) {
            "Maximum cohorts per year (${Cohort.MAX_COHORTS_PER_YEAR}) exceeded"
        }

        // Validate unique name
        require(_cohorts.none { it.name.equals(cohort.name, ignoreCase = true) }) {
            "Cohort name must be unique within school"
        }

        _cohorts.add(cohort)
        cohort.school = this
    }

    fun removeCohort(cohort: Cohort) {
        _cohorts.remove(cohort)
    }
}
