package com.projecthub.base.task.domain.validation;

import com.projecthub.base.shared.exception.ValidationException;
import com.projecthub.base.task.domain.entity.Task;
import com.projecthub.base.task.domain.value.TaskValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class TaskValidator {

    public TaskValidator() {
    }

    public void validateTasks(final Set<Task> tasks) {
        TaskValidator.log.debug("Validating {} tasks", Integer.valueOf(tasks.size()));
        tasks.forEach(this::validateTask);
    }

    private void validateTask(final Task task) {
        if (null == Task.getValue()) {
            throw new ValidationException("Task value cannot be null");
        }
        TaskValidator.validateTaskBasics(task);
        TaskValidator.validateTaskDates(task);
        TaskValidator.validateTaskAssignments(task);
    }

    private static void validateTaskBasics(final Task task) {
        final TaskValue value = Task.getValue();
        if (null == value.name() || value.name().trim().isEmpty()) {
            throw new ValidationException("Task name is required");
        }
        if (null == value.status()) {
            throw new ValidationException("Task status is required");
        }
    }

    private static void validateTaskDates(final Task task) {
        final TaskValue value = Task.getValue();
        if (null != value.dueDate() && null != value.plannedStartDate()
            && value.dueDate().isBefore(value.plannedStartDate())) {
            throw new ValidationException("Task due date cannot be before planned start date");
        }
    }

    private static void validateTaskAssignments(final Task task) {
        final TaskValue value = Task.getValue();
        if (value.isStarted() && null == value.assignee()) {
            throw new ValidationException("Started task must have an assignee");
        }
    }
}
