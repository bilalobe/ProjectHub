package com.projecthub.core.mappers;

import com.projecthub.core.dto.TeamDTO;
import com.projecthub.core.models.Team;
import org.mapstruct.*;

/**
 * Mapper for Team entity with protected collection handling.
 * Students collection is managed separately through service layer.
 */
@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TeamMapper extends BaseMapper<Team, TeamDTO> {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "students", ignore = true)
    Team toEntity(TeamDTO dto);

    @Mapping(target = "students", ignore = true)
    TeamDTO toDto(Team entity);

    @Named("toSummary")
    @Mapping(target = "students", ignore = true)
    TeamDTO toSummaryDto(Team entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "students", ignore = true)
    void updateEntityFromDto(TeamDTO dto, @MappingTarget Team entity);
}