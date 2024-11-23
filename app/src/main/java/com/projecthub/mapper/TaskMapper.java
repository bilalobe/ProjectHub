package com.projecthub.mapper;

import com.projecthub.dto.TaskSummary;
import com.projecthub.model.AppUser;
import com.projecthub.model.Project;
import com.projecthub.model.Task;

public class TaskMapper {

    public static Task toTask(TaskSummary taskSummary, Project project, AppUser assignedUser) {
        Task task = new Task();
        task.setId(taskSummary.getId());
        task.setName(taskSummary.getName());
        task.setDescription(taskSummary.getDescription());
        task.setStatus(taskSummary.getStatus());
        task.setDueDate(taskSummary.getDueDate() != null ? java.time.LocalDate.parse(taskSummary.getDueDate()) : null);
        task.setProject(project);
        task.setAssignedUser(assignedUser);
        return task;
    }

    public static TaskSummary toTaskSummary(Task task) {
        return new TaskSummary(task);
    }
}