package com.projecthub.mapper;

import com.projecthub.dto.TaskSummary;
import com.projecthub.model.AppUser;
import com.projecthub.model.Project;
import com.projecthub.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toTask(TaskSummary taskSummary, Project project, AppUser assignedUser) {
        Task task = new Task();
        task.setId(taskSummary.getId());
        task.setName(taskSummary.getName());
        task.setDescription(taskSummary.getDescription());
        task.setStatus(taskSummary.getStatus());
        task.setDueDate(taskSummary.getDueDate());
        task.setProject(project);
        task.setAssignedUser(assignedUser);
        return task;
    }

    public TaskSummary toTaskSummary(Task task) {
        return new TaskSummary(task);
    }

    public void updateTaskFromSummary(TaskSummary taskSummary, Task existingTask, Project project, AppUser assignedUser) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}