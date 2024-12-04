package com.projecthub.mapper;

import com.projecthub.dto.TaskDTO;
import com.projecthub.model.Project;
import com.projecthub.model.AppUser;
import com.projecthub.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {ProjectMapper.class, AppUserMapper.class})
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "assignedUserId", target = "assignedUser.id")
    Task toTask(TaskDTO taskDTO);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "assignedUser.id", target = "assignedUserId")
    TaskDTO toTaskDTO(Task task);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "assignedUserId", target = "assignedUser.id")
    void updateTaskFromDTO(TaskDTO taskDTO, @MappingTarget Task task);

    default Task toTask(TaskDTO taskDTO, Project project, AppUser assignedUser) {
        Task task = toTask(taskDTO);
        task.setProject(project);
        task.setAssignedUser(assignedUser);
        return task;
    }

    default void updateTaskFromDTO(TaskDTO taskDTO, Task task, Project project, AppUser assignedUser) {
        updateTaskFromDTO(taskDTO, task);
        task.setProject(project);
        task.setAssignedUser(assignedUser);
    }
}