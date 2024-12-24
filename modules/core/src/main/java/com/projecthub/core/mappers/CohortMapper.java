package com.projecthub.core.mappers;

import com.projecthub.core.dto.CohortDTO;
import com.projecthub.core.models.Cohort;
import org.mapstruct.*;

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
}