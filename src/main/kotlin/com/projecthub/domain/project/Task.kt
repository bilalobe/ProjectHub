package com.projecthub.domain.project

import com.projecthub.domain.BaseEntity
import com.projecthub.domain.Entity
import com.projecthub.domain.user.UserId
import jakarta.persistence.*
import java.time.Instant

/**
 * Entity representing a task within a project.
 */
@jakarta.persistence.Entity
@Table(name = "tasks")
class Task(
    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var description: String,

    @Embedded
    var status: TaskStatus = TaskStatus.TODO,

    @Column(name = "due_date")
    var dueDate: Instant? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    val project: Project,

    @Column(name = "assignee_id")
    var assigneeId: UserId? = null
) : BaseEntity(), Entity<Task> {

    companion object {
        fun create(
            title: String,
            description: String,
            project: Project,
            dueDate: Instant? = null,
            assigneeId: UserId? = null
        ): Task {
            require(title.isNotBlank()) { "Task title cannot be empty" }

            val task = Task(
                title = title,
                description = description,
                dueDate = dueDate,
                project = project,
                assigneeId = assigneeId
            )

            // Add the task to the project
            project.addTask(task)

            return task
        }
    }

    /**
     * Updates task details.
     */
    fun update(
        title: String? = null,
        description: String? = null,
        status: TaskStatus? = null,
        dueDate: Instant? = null,
        assigneeId: UserId? = this.assigneeId
    ) {
        this.title = title?.takeIf { it.isNotBlank() } ?: this.title
        this.description = description?.takeIf { it.isNotBlank() } ?: this.description
        this.status = status ?: this.status
        this.dueDate = dueDate ?: this.dueDate
        this.assigneeId = assigneeId
    }

    /**
     * Assigns the task to a user.
     */
    fun assignTo(userId: UserId) {
        this.assigneeId = userId
    }

    /**
     * Marks the task as complete.
     */
    fun complete() {
        this.status = TaskStatus.DONE
    }
}
