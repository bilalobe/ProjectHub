package com.projecthub.base.project.infrastructure.mapper;

import com.projecthub.base.milestone.domain.entity.Milestone;
import com.projecthub.base.milestone.infrastructure.mapper.MilestoneMapper;
import com.projecthub.base.project.api.command.CreateProjectCommand;
import com.projecthub.base.project.api.command.UpdateProjectCommand;
import com.projecthub.base.project.api.dto.ProjectDTO;
import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.shared.domain.entity.BaseEntity;
import com.projecthub.base.task.domain.entity.Task;
import com.projecthub.base.task.infrastructure.mapper.TaskMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
    uses = {MilestoneMapper.class, TaskMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
/*
 * Mapper for Project entity and DTO conversions.
 */
public interface ProjectMapper {

    @Mapping(target = "teamId", source = "team.id")
    @Mapping(target = "teamName", source = "team.name")
    ProjectDTO toDto(Project project);

    @Mapping(target = "team", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "milestones", ignore = true)
    Project toEntity(ProjectDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromDto(ProjectDTO dto, @MappingTarget Project project);

    @Mapping(target = "team", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "milestones", ignore = true)
    Project fromCreateCommand(CreateProjectCommand command);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateFromCommand(UpdateProjectCommand command, @MappingTarget Project project);

    default Set<UUID> mapMilestoneIds(final List<Milestone> milestones) {
        if (null == milestones) return new HashSet<>();
        return milestones.stream()
            .map(BaseEntity::getId)
            .collect(Collectors.toSet());
    }

    default Set<UUID> mapTaskIds(final List<Task> tasks) {
        if (null == tasks) return new HashSet<>();
        return tasks.stream()
            .map(BaseEntity::getId)
            .collect(Collectors.toSet());
    }
}
