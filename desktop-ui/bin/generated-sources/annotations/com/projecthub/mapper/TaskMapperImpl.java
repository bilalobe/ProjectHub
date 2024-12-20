package com.projecthub.mapper;

import com.projecthub.dto.TaskDTO;
import com.projecthub.model.AppUser;
import com.projecthub.model.Project;
import com.projecthub.model.Task;

import java.util.UUID;
import javax.annotation.processing.Generated;

import org.springframework.stereotype.Component;

@Generated(
        value = "org.mapstruct.ap.MappingProcessor",
        date = "2024-12-08T14:18:24+0100",
        comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.40.0.z20241112-1021, environment: Java 17.0.13 (Eclipse Adoptium)"
)
@Component
public class TaskMapperImpl implements TaskMapper {

    @Override
    public Task toTask(TaskDTO taskDTO) {
        if (taskDTO == null) {
            return null;
        }

        Task task = new Task();

        task.setProject(taskDTOToProject(taskDTO));
        task.setAssignedUser(taskDTOToAppUser(taskDTO));
        task.setId(taskDTO.getId());
        task.setName(taskDTO.getName());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setDueDate(taskDTO.getDueDate());
        if (taskDTO.getCreatedAt() != null) {
            task.setCreatedAt(taskDTO.getCreatedAt().atStartOfDay());
        }
        if (taskDTO.getUpdatedAt() != null) {
            task.setUpdatedAt(taskDTO.getUpdatedAt().atStartOfDay());
        }
        task.setCreatedBy(taskDTO.getCreatedBy());
        task.setDeleted(taskDTO.isDeleted());

        return task;
    }

    @Override
    public TaskDTO toTaskDTO(Task task) {
        if (task == null) {
            return null;
        }

        TaskDTO taskDTO = new TaskDTO();

        taskDTO.setProjectId(taskProjectId(task));
        taskDTO.setAssignedUserId(taskAssignedUserId(task));
        taskDTO.setId(task.getId());
        taskDTO.setName(task.getName());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setStatus(task.getStatus());
        taskDTO.setDueDate(task.getDueDate());
        if (task.getCreatedAt() != null) {
            taskDTO.setCreatedAt(task.getCreatedAt().toLocalDate());
        }
        taskDTO.setCreatedBy(task.getCreatedBy());
        taskDTO.setDeleted(task.isDeleted());
        if (task.getUpdatedAt() != null) {
            taskDTO.setUpdatedAt(task.getUpdatedAt().toLocalDate());
        }

        return taskDTO;
    }

    @Override
    public void updateTaskFromDTO(TaskDTO taskDTO, Task task) {
        if (taskDTO == null) {
            return;
        }

        if (task.getProject() == null) {
            task.setProject(new Project());
        }
        taskDTOToProject1(taskDTO, task.getProject());
        if (task.getAssignedUser() == null) {
            task.setAssignedUser(new AppUser());
        }
        taskDTOToAppUser1(taskDTO, task.getAssignedUser());
        task.setId(taskDTO.getId());
        task.setName(taskDTO.getName());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setDueDate(taskDTO.getDueDate());
        if (taskDTO.getCreatedAt() != null) {
            task.setCreatedAt(taskDTO.getCreatedAt().atStartOfDay());
        } else {
            task.setCreatedAt(null);
        }
        if (taskDTO.getUpdatedAt() != null) {
            task.setUpdatedAt(taskDTO.getUpdatedAt().atStartOfDay());
        } else {
            task.setUpdatedAt(null);
        }
        task.setCreatedBy(taskDTO.getCreatedBy());
        task.setDeleted(taskDTO.isDeleted());
    }

    protected Project taskDTOToProject(TaskDTO taskDTO) {
        if (taskDTO == null) {
            return null;
        }

        Project project = new Project();

        project.setId(taskDTO.getProjectId());

        return project;
    }

    protected AppUser taskDTOToAppUser(TaskDTO taskDTO) {
        if (taskDTO == null) {
            return null;
        }

        AppUser appUser = new AppUser();

        appUser.setId(taskDTO.getAssignedUserId());

        return appUser;
    }

    private UUID taskProjectId(Task task) {
        Project project = task.getProject();
        if (project == null) {
            return null;
        }
        return project.getId();
    }

    private UUID taskAssignedUserId(Task task) {
        AppUser assignedUser = task.getAssignedUser();
        if (assignedUser == null) {
            return null;
        }
        return assignedUser.getId();
    }

    protected void taskDTOToProject1(TaskDTO taskDTO, Project mappingTarget) {
        if (taskDTO == null) {
            return;
        }

        mappingTarget.setId(taskDTO.getProjectId());
    }

    protected void taskDTOToAppUser1(TaskDTO taskDTO, AppUser mappingTarget) {
        if (taskDTO == null) {
            return;
        }

        mappingTarget.setId(taskDTO.getAssignedUserId());
    }
}
