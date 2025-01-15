package com.projecthub.base.milestone.infrastructure.mapper;

import com.projecthub.base.milestone.api.dto.MilestoneDTO;
import com.projecthub.base.milestone.domain.entity.Milestone;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MilestoneMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "dependencyIds", source = "dependencies")
    MilestoneDTO toDto(Milestone milestone);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "dependencies", ignore = true)
    Milestone toEntity(MilestoneDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "dependencies", ignore = true)
    void updateEntityFromDto(MilestoneDTO dto, @MappingTarget Milestone milestone);

    @Named("mapDependencyIds")
    default Set<UUID> mapDependencies(final Set<Milestone> dependencies) {
        if (null == dependencies) return new HashSet<>();
        return dependencies.stream()
            .map(Milestone::getId)
            .collect(Collectors.toSet());
    }
}
