package com.projecthub.infrastructure.persistence.milestone.jpa

import com.projecthub.domain.milestone.MilestoneStatus
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "milestones")
class MilestoneJpaEntity(
    @Id
    var id: String,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, length = 1000)
    var description: String,

    @Column(name = "project_id", nullable = false)
    var projectId: String,

    @Column(name = "due_date", nullable = false)
    var dueDate: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: MilestoneStatus = MilestoneStatus.PENDING,

    @Column(nullable = false)
    var progress: Int = 0,

    @Column(name = "assignee_id")
    var assigneeId: String? = null,

    @Column(name = "completion_date")
    var completionDate: LocalDate? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @ManyToMany
    @JoinTable(
        name = "milestone_dependencies",
        joinColumns = [JoinColumn(name = "milestone_id")],
        inverseJoinColumns = [JoinColumn(name = "dependency_id")]
    )
    var dependencies: MutableSet<MilestoneJpaEntity> = mutableSetOf()

    // No-args constructor required by JPA
    constructor() : this(
        id = "",
        name = "",
        description = "",
        projectId = "",
        dueDate = LocalDate.now(),
        status = MilestoneStatus.PENDING,
        progress = 0,
        assigneeId = null,
        completionDate = null
    )
}
