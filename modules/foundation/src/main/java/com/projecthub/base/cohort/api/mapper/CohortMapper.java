package com.projecthub.base.cohort.api.mapper;

import com.projecthub.base.cohort.api.dto.CohortDTO;
import com.projecthub.base.cohort.api.input.CreateCohortInput;
import com.projecthub.base.cohort.api.input.UpdateCohortInput;
import com.projecthub.base.cohort.domain.command.CreateCohortCommand;
import com.projecthub.base.cohort.domain.command.UpdateCohortCommand;
import com.projecthub.base.cohort.domain.entity.Cohort;
import com.projecthub.base.shared.api.mapper.BaseMapper;
import org.mapstruct.*;

import java.util.UUID;

/**
 * Mapper for Cohort entity with protected relationship handling.
 */
@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CohortMapper extends BaseMapper<CohortDTO, Cohort> {

    @Override
    @Mapping(target = "school.id", source = "schoolId")
    @Mapping(target = "id", ignore = true)
    Cohort toEntity(CohortDTO dto);

    @Override
    @Mapping(source = "school.id", target = "schoolId")
    CohortDTO toDto(Cohort entity);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "school.id", source = "schoolId")
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(CohortDTO dto, @MappingTarget Cohort entity);

    CreateCohortCommand toCreateCommand(CreateCohortInput input);

    UpdateCohortCommand toUpdateCommand(UpdateCohortInput input, UUID id);
}
