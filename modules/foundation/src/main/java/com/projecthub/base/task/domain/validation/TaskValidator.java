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

    public void validateTasks(Set<Task> tasks) {
        log.debug("Validating {} tasks", tasks.size());
        tasks.forEach(this::validateTask);
    }

    private void validateTask(Task task) {
        if (task.getValue() == null) {
            throw new ValidationException("Task value cannot be null");
        }
        validateTaskBasics(task);
        validateTaskDates(task);
        validateTaskAssignments(task);
    }

    private void validateTaskBasics(Task task) {
        TaskValue value = task.getValue();
        if (value.name() == null || value.name().trim().isEmpty()) {
            throw new ValidationException("Task name is required");
        }
        if (value.status() == null) {
            throw new ValidationException("Task status is required");
        }
    }

    private void validateTaskDates(Task task) {
        TaskValue value = task.getValue();
        if (value.dueDate() != null && value.plannedStartDate() != null
            && value.dueDate().isBefore(value.plannedStartDate())) {
            throw new ValidationException("Task due date cannot be before planned start date");
        }
    }

    private void validateTaskAssignments(Task task) {
        TaskValue value = task.getValue();
        if (value.isStarted() && value.assignee() == null) {
            throw new ValidationException("Started task must have an assignee");
        }
    }
}
