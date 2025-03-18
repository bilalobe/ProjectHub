package com.projecthub.domain.team

import com.projecthub.domain.BaseEntity
import com.projecthub.domain.cohort.Cohort
import com.projecthub.domain.user.User
import jakarta.persistence.*
import java.time.Instant

/**
 * Represents a team within the ProjectHub system.
 * Teams are groups of students and mentors working together on projects.
 * Each team belongs to a specific cohort within a school.
 *
 * Business rules:
 * - Each team must have a unique name within its school
 * - Teams must belong to exactly one cohort
 * - Teams can have between 1-10 students
 * - Teams can have between 1-5 members (mentors, instructors)
 */
@Entity
@Table(
    name = "teams",
    indexes = [
        Index(name = "idx_team_name", columnList = "name"),
        Index(name = "idx_team_cohort", columnList = "cohort_id")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uc_team_name_school", columnNames = ["name", "school_id"])
    ]
)
class Team(
    @Column(nullable = false)
    var name: String,

    @Column(length = 200)
    var communicationChannel: String? = null,

    @Column(length = 500)
    var meetingSchedule: String? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: TeamStatus = TeamStatus.ACTIVE,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cohort_id", nullable = false)
    var cohort: Cohort
) : BaseEntity() {

    @OneToMany(mappedBy = "team", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val _students: MutableList<Student> = mutableListOf()
    val students: List<Student> get() = _students.toList()

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "team_members",
        joinColumns = [JoinColumn(name = "team_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    private val _members: MutableSet<User> = mutableSetOf()
    val members: Set<User> get() = _members.toSet()

    @Embedded
    var position: TeamPosition? = null

    init {
        require(name.isNotBlank()) { "Team name is mandatory" }
        require(name.length <= 100) { "Team name must be less than 100 characters" }
    }

    /**
     * Adds a student to the team with validation
     */
    fun addStudent(student: Student) {
        require(_students.size < MAX_STUDENTS) {
            "Team cannot have more than $MAX_STUDENTS students"
        }
        _students.add(student)
    }

    /**
     * Adds a member (mentor, instructor) to the team with validation
     */
    fun addMember(member: User) {
        require(_members.size < MAX_MEMBERS) {
            "Team cannot have more than $MAX_MEMBERS members"
        }
        _members.add(member)
    }

    /**
     * Assigns a position to the team with validation
     */
    fun assignPosition(row: Int, column: Int) {
        val newPosition = TeamPosition(row, column)
        // Validation happens at the service layer through TeamValidator
        position = newPosition
    }

    /**
     * Removes a student from the team
     */
    fun removeStudent(student: Student) {
        require(_students.contains(student)) { "Student is not in this team" }
        require(_students.size > 1) { "Team must have at least one student" }
        _students.remove(student)
    }

    /**
     * Removes a member from the team
     */
    fun removeMember(member: User) {
        require(_members.contains(member)) { "Member is not in this team" }
        require(_members.size > 1) { "Team must have at least one member" }
        _members.remove(member)
    }

    companion object {
        const val MAX_STUDENTS = 10
        const val MAX_MEMBERS = 5
    }
}

enum class TeamStatus {
    ACTIVE,
    INACTIVE,
    ARCHIVED
}

@Embeddable
data class TeamPosition(
    @Column(name = "position_row", nullable = false)
    val row: Int,

    @Column(name = "position_column", nullable = false)
    val column: Int
)
