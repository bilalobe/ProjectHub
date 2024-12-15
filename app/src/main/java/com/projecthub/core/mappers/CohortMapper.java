package com.projecthub.core.mappers;

import com.projecthub.core.dto.CohortDTO;
import com.projecthub.core.models.Cohort;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CohortMapper {

    @Mapping(target = "school.id", source = "schoolId")
    Cohort toCohort(CohortDTO cohortDTO);

    @Mapping(source = "school.id", target = "schoolId")
    CohortDTO toCohortDTO(Cohort cohort);

    @Mapping(target = "school.id", source = "schoolId")
    void updateCohortFromDTO(CohortDTO cohortDTO, @MappingTarget Cohort cohort);
}