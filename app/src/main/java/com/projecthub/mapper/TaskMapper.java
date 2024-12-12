package com.projecthub.mapper;

import com.projecthub.dto.TaskDTO;
import com.projecthub.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "assignedUser.id", source = "assignedUserId")
    Task toTask(TaskDTO taskDTO);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "assignedUser.id", target = "assignedUserId")
    TaskDTO toTaskDTO(Task task);

    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "assignedUser.id", source = "assignedUserId")
    void updateTaskFromDTO(TaskDTO taskDTO, @MappingTarget Task task);

}