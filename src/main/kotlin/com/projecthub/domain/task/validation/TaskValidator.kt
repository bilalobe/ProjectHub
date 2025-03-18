package com.projecthub.domain.task.validation

import com.projecthub.domain.validation.ValidationResult
import com.projecthub.domain.validation.Validator
import com.projecthub.domain.task.Task
import org.springframework.stereotype.Component

@Component
class TaskValidator : Validator<Task> {

    fun validateTasks(tasks: Set<Task>): ValidationResult {
        return ValidationResult.Builder().apply {
            tasks.forEach { task ->
                validateTaskDependencies(task, tasks)
            }
        }.build()
    }

    private fun ValidationResult.Builder.validateTaskDependencies(task: Task, allTasks: Set<Task>) {
        // Add dependency validation logic here when implementing task dependencies
        // This will be useful when we migrate the dependency features
    }

    fun validateCreate(task: Task): ValidationResult {
        return ValidationResult.Builder().apply {
            validateBasics(task)
            validateDates(task)
            validateAssignments(task)
        }.build()
    }

    private fun ValidationResult.Builder.validateBasics(task: Task) {
        if (task.name.isBlank()) {
            addError("Task name is required")
        }
    }

    private fun ValidationResult.Builder.validateDates(task: Task) {
        task.plannedStartDate?.let { start ->
            task.dueDate?.let { due ->
                if (due.isBefore(start)) {
                    addError("Task due date cannot be before planned start date")
                }
            }
        }
    }

    private fun ValidationResult.Builder.validateAssignments(task: Task) {
        if (task.status.isStarted && task.assigneeId == null) {
            addError("Started task must have an assignee")
        }
    }
}