package com.projecthub.domain.cohort

import com.projecthub.domain.AggregateRoot
import com.projecthub.domain.cohort.event.CohortCreatedEvent
import com.projecthub.domain.cohort.event.CohortUpdatedEvent
import com.projecthub.domain.cohort.event.StudentAssignedToCohortEvent
import com.projecthub.domain.cohort.event.CohortCompletedEvent
import com.projecthub.domain.team.Team
import java.time.LocalDate
import java.time.LocalDateTime

class Cohort private constructor(
    val id: String,
    var name: String,
    var code: String,
    var schoolId: String,
    var assignment: CohortAssignment,
    var startTerm: LocalDate,
    var endTerm: LocalDate,
    var isArchived: Boolean = false,
    var archiveReason: String? = null,
    var seatingConfig: SeatingConfiguration? = null,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
) : AggregateRoot<String>() {

    override fun getId(): String = id

    private val _teams: MutableSet<Team> = mutableSetOf()
    val teams: Set<Team> get() = _teams.toSet()

    companion object {
        const val MAX_STUDENTS = 50
        const val MIN_STUDENTS = 10
        const val MAX_COHORTS_PER_YEAR = 4

        /**
         * Creates a new Cohort instance and registers a cohort created event
         */
        fun create(
            id: String,
            name: String,
            code: String,
            schoolId: String,
            capacity: Int,
            startTerm: LocalDate,
            endTerm: LocalDate
        ): Cohort {
            val cohortAssignment = CohortAssignment(maxStudents = capacity, maxTeams = capacity / 5)

            val cohort = Cohort(
                id = id,
                name = name,
                code = code,
                schoolId = schoolId,
                assignment = cohortAssignment,
                startTerm = startTerm,
                endTerm = endTerm
            )

            // Register domain event for new cohort creation
            cohort.registerEvent(
                CohortCreatedEvent(
                    cohortId = id,
                    name = name,
                    schoolId = schoolId,
                    capacity = capacity
                )
            )

            return cohort
        }
    }

    val isActive: Boolean
        get() = !isArchived &&
            LocalDate.now().isAfter(startTerm) &&
            LocalDate.now().isBefore(endTerm)

    val currentEnrollment: Int
        get() = teams.sumOf { it.members.size }

    fun addTeam(team: Team) {
        require(!isArchived) { "Cannot add team to archived cohort" }
        require(_teams.size < assignment.maxTeams) { "Maximum number of teams reached" }
        _teams.add(team)
        updatedAt = LocalDateTime.now()
    }

    fun assignStudent(studentId: String) {
        require(!isArchived) { "Cannot assign student to archived cohort" }
        require(currentEnrollment < assignment.maxStudents) { "Maximum student capacity reached" }

        // In a real implementation, you would assign the student to a team or maintain a separate collection
        // For this example, we'll just emit the event
        registerEvent(
            StudentAssignedToCohortEvent(
                cohortId = id,
                studentId = studentId,
                assignmentDate = LocalDateTime.now().toString()
            )
        )

        updatedAt = LocalDateTime.now()
    }

    fun removeTeam(team: Team) {
        require(_teams.contains(team)) { "Team is not part of this cohort" }
        _teams.remove(team)
        updatedAt = LocalDateTime.now()
    }

    fun complete() {
        require(!isArchived) { "Cannot complete an archived cohort" }
        require(!LocalDate.now().isBefore(endTerm)) { "Cannot complete cohort before end term" }
        isArchived = true
        archiveReason = "Completed"
        updatedAt = LocalDateTime.now()

        registerEvent(
            CohortCompletedEvent(
                cohortId = id,
                completionDate = LocalDateTime.now().toString()
            )
        )
    }

    fun archive(reason: String) {
        require(!isArchived) { "Cohort is already archived" }
        require(reason.isNotBlank()) { "Archive reason is required" }
        isArchived = true
        archiveReason = reason
        updatedAt = LocalDateTime.now()
    }

    fun updateDetails(name: String, capacity: Int, startTerm: LocalDate, endTerm: LocalDate) {
        require(!isArchived) { "Cannot update archived cohort" }
        require(!startTerm.isAfter(endTerm)) { "Start term cannot be after end term" }
        require(capacity in MIN_STUDENTS..MAX_STUDENTS) {
            "Capacity must be between $MIN_STUDENTS and $MAX_STUDENTS"
        }

        this.name = name
        this.startTerm = startTerm
        this.endTerm = endTerm
        this.assignment = assignment.copy(maxStudents = capacity, maxTeams = capacity / 5)
        this.updatedAt = LocalDateTime.now()

        registerEvent(
            CohortUpdatedEvent(
                cohortId = id,
                name = name,
                capacity = capacity
            )
        )
    }

    /**
     * Configures the seating arrangement for this cohort
     */
    fun configureSeating(rows: Int, columns: Int, blockedPositions: Set<BlockedPosition> = emptySet()) {
        require(!isArchived) { "Cannot update seating configuration for archived cohort" }
        seatingConfig = SeatingConfiguration(rows, columns, blockedPositions)
        updatedAt = LocalDateTime.now()
    }
}
