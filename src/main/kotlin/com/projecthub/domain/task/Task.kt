package com.projecthub.domain.task

import com.projecthub.domain.BaseEntity
import com.projecthub.domain.user.UserId
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "tasks")
class Task private constructor(
    @Column(nullable = false)
    var name: String,

    @Column(length = 1000)
    var description: String? = null,

    @Column(name = "planned_start_date")
    var plannedStartDate: LocalDate? = null,

    @Column(name = "due_date")
    var dueDate: LocalDate? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: TaskStatus = TaskStatus.TODO,

    @Column(name = "assignee_id")
    var assigneeId: UserId? = null
) : BaseEntity() {

    /**
     * Creates a new task with validation
     */
    companion object {
        fun create(
            name: String,
            description: String? = null,
            plannedStartDate: LocalDate? = null,
            dueDate: LocalDate? = null,
            assigneeId: UserId? = null
        ): Task {
            require(name.isNotBlank()) { "Task name is required" }

            plannedStartDate?.let { start ->
                dueDate?.let { due ->
                    require(!due.isBefore(start)) {
                        "Task due date cannot be before planned start date"
                    }
                }
            }

            return Task(
                name = name,
                description = description,
                plannedStartDate = plannedStartDate,
                dueDate = dueDate,
                assigneeId = assigneeId
            )
        }
    }

    /**
     * Updates task status with validation
     */
    fun updateStatus(newStatus: TaskStatus) {
        if (newStatus.isStarted && assigneeId == null) {
            throw IllegalStateException("Started task must have an assignee")
        }
        status = newStatus
    }

    /**
     * Assigns the task to a user
     */
    fun assignTo(userId: UserId) {
        assigneeId = userId
    }

    /**
     * Unassigns the task
     */
    fun unassign() {
        require(!status.isStarted) { "Cannot unassign a started task" }
        assigneeId = null
    }

    /**
     * Updates task dates with validation
     */
    fun updateDates(
        plannedStartDate: LocalDate? = null,
        dueDate: LocalDate? = null
    ) {
        if (plannedStartDate != null) {
            this.plannedStartDate = plannedStartDate
        }
        if (dueDate != null) {
            this.dueDate = dueDate
        }

        this.plannedStartDate?.let { start ->
            this.dueDate?.let { due ->
                require(!due.isBefore(start)) {
                    "Task due date cannot be before planned start date"
                }
            }
        }
    }

    val isCompleted: Boolean get() = status == TaskStatus.DONE
}

enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    REVIEW,
    BLOCKED,
    DONE;

    val isStarted: Boolean get() = this == IN_PROGRESS || this == REVIEW
}
