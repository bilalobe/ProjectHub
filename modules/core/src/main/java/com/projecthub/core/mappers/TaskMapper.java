package com.projecthub.core.mappers;

import com.projecthub.core.dto.TaskDTO;
import com.projecthub.core.models.Task;
import org.mapstruct.*;

/**
 * Mapper for Task entity and DTO conversions.
 * Handles protected fields and maintains referential integrity.
 */
@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper extends BaseMapper<TaskDTO, Task> {

    @Override
    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "assignedUser.id", source = "assignedUserId")
    Task toEntity(TaskDTO dto);

    @Override
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "assignedUser.id", target = "assignedUserId")
    TaskDTO toDto(Task entity);

    @Override
    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "assignedUser.id", source = "assignedUserId")
    void updateEntityFromDto(TaskDTO dto, @MappingTarget Task entity);
}