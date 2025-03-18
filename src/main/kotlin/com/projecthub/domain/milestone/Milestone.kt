package com.projecthub.domain.milestone

import com.projecthub.domain.AggregateRoot
import com.projecthub.domain.milestone.event.MilestoneCreatedEvent
import com.projecthub.domain.milestone.event.MilestoneCompletedEvent
import com.projecthub.domain.milestone.event.MilestoneDueDateChangedEvent
import com.projecthub.domain.milestone.event.MilestoneAssignedEvent
import com.projecthub.domain.task.Task
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class Milestone private constructor(
    val id: String,
    var name: String,
    var description: String,
    var dueDate: LocalDate,
    var status: MilestoneStatus = MilestoneStatus.PENDING,
    var progress: Int = 0,
    var projectId: String,
    var assigneeId: String? = null,
    var completionDate: LocalDate? = null,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
) : AggregateRoot<String>() {

    override fun getId(): String = id

    private val _tasks: MutableSet<Task> = mutableSetOf()
    val tasks: Set<Task> get() = _tasks.toSet()

    private val _dependencies: MutableSet<Milestone> = mutableSetOf()
    val dependencies: Set<Milestone> get() = _dependencies.toSet()

    val isCompleted: Boolean get() = status == MilestoneStatus.COMPLETED
    val isBlocked: Boolean get() = status == MilestoneStatus.BLOCKED

    companion object {
        /**
         * Creates a new Milestone instance and registers a milestone created event
         */
        fun create(
            id: String,
            name: String,
            description: String,
            projectId: String,
            dueDate: LocalDate
        ): Milestone {
            val milestone = Milestone(
                id = id,
                name = name,
                description = description,
                dueDate = dueDate,
                projectId = projectId
            )

            // Register domain event for new milestone creation
            milestone.registerEvent(
                MilestoneCreatedEvent(
                    milestoneId = id,
                    name = name,
                    projectId = projectId,
                    dueDate = dueDate.toString(),
                    description = description
                )
            )

            return milestone
        }
    }

    fun updateStatus(newStatus: MilestoneStatus) {
        validateStatusTransition(newStatus)
        status = newStatus
        updatedAt = LocalDateTime.now()

        if (newStatus == MilestoneStatus.COMPLETED) {
            completionDate = LocalDate.now()
            progress = 100

            registerEvent(
                MilestoneCompletedEvent(
                    milestoneId = id,
                    projectId = projectId,
                    completionDate = completionDate.toString()
                )
            )
        }
    }

    fun addTask(task: Task) {
        _tasks.add(task)
        updateProgress()
        updatedAt = LocalDateTime.now()
    }

    fun assignTo(assigneeId: String) {
        this.assigneeId = assigneeId
        updatedAt = LocalDateTime.now()

        registerEvent(
            MilestoneAssignedEvent(
                milestoneId = id,
                projectId = projectId,
                assigneeId = assigneeId
            )
        )
    }

    fun changeDueDate(newDueDate: LocalDate) {
        val oldDueDate = this.dueDate
        this.dueDate = newDueDate
        updatedAt = LocalDateTime.now()

        registerEvent(
            MilestoneDueDateChangedEvent(
                milestoneId = id,
                projectId = projectId,
                oldDueDate = oldDueDate.toString(),
                newDueDate = newDueDate.toString()
            )
        )
    }

    fun addDependency(dependency: Milestone) {
        require(!hasCyclicDependencies(dependency)) { "Adding this dependency would create a cycle" }
        require(!dependency.dueDate.isAfter(this.dueDate)) { "Dependency due date must be before milestone due date" }
        _dependencies.add(dependency)
        updatedAt = LocalDateTime.now()
    }

    fun updateProgress() {
        val completedTasks = tasks.count { it.isCompleted }
        val newProgress = if (tasks.isEmpty()) 0
        else ((completedTasks.toDouble() / tasks.size) * 100).toInt()

        require(!(status == MilestoneStatus.COMPLETED && newProgress != 100)) {
            "Completed milestone must have 100% progress"
        }

        progress = newProgress
        updatedAt = LocalDateTime.now()
    }

    private fun validateStatusTransition(newStatus: MilestoneStatus) {
        val validTransitions = when (status) {
            MilestoneStatus.PENDING -> setOf(
                MilestoneStatus.IN_PROGRESS,
                MilestoneStatus.BLOCKED,
                MilestoneStatus.CANCELLED
            )

            MilestoneStatus.IN_PROGRESS -> setOf(
                MilestoneStatus.COMPLETED,
                MilestoneStatus.BLOCKED,
                MilestoneStatus.CANCELLED
            )

            MilestoneStatus.BLOCKED -> setOf(
                MilestoneStatus.PENDING,
                MilestoneStatus.CANCELLED
            )

            MilestoneStatus.COMPLETED -> emptySet()
            MilestoneStatus.CANCELLED -> emptySet()
        }

        require(validTransitions.contains(newStatus)) {
            "Invalid status transition from $status to $newStatus"
        }

        when (newStatus) {
            MilestoneStatus.IN_PROGRESS -> {
                require(dependencies.all { it.isCompleted }) {
                    "Cannot start milestone with incomplete dependencies"
                }
            }

            MilestoneStatus.COMPLETED -> {
                require(tasks.all { it.isCompleted }) {
                    "Cannot complete milestone with incomplete tasks"
                }
                require(progress == 100) {
                    "Cannot complete milestone with progress less than 100%"
                }
            }

            MilestoneStatus.BLOCKED -> {
                val hasBlockedDependency = dependencies.any { it.isBlocked }
                val hasIncompleteDependency = dependencies.any { !it.isCompleted }
                val hasUnassignedTasks = tasks.any { it.assignee == null }
                require(hasBlockedDependency || hasIncompleteDependency || hasUnassignedTasks) {
                    "Cannot block milestone without blocked/incomplete dependencies or unassigned tasks"
                }
            }

            MilestoneStatus.CANCELLED -> {
                require(!isCompleted) { "Cannot cancel completed milestone" }
            }

            MilestoneStatus.PENDING -> {
                require(status == MilestoneStatus.BLOCKED) {
                    "Can only transition to PENDING from BLOCKED status"
                }
                require(dependencies.all { it.isCompleted }) {
                    "All dependencies must be completed before transitioning to PENDING"
                }
            }
        }
    }

    private fun hasCyclicDependencies(newDependency: Milestone, visited: MutableSet<String> = mutableSetOf()): Boolean {
        if (!visited.add(this.id)) return true
        if (this == newDependency) return true

        return dependencies.any { it.hasCyclicDependencies(newDependency, visited) }
    }
}

enum class MilestoneStatus {
    PENDING,
    IN_PROGRESS,
    BLOCKED,
    COMPLETED,
    CANCELLED
}
