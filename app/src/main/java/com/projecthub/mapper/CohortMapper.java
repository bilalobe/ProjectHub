package com.projecthub.mapper;

import com.projecthub.dto.CohortDTO;
import com.projecthub.model.Cohort;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {SchoolMapper.class})
public interface CohortMapper {

    CohortMapper INSTANCE = Mappers.getMapper(CohortMapper.class);

    @Mapping(source = "schoolId", target = "school.id")
    @Mapping(target = "teams", ignore = true) // Assuming teams are managed separately
    @Mapping(target = "deleted", ignore = true) // Assuming deleted is managed separately
    Cohort toCohort(CohortDTO cohortDTO);

    @Mapping(source = "school.id", target = "schoolId")
    CohortDTO toCohortDTO(Cohort cohort);

    @Mapping(source = "schoolId", target = "school.id")
    @Mapping(target = "teams", ignore = true) // Assuming teams are managed separately
    @Mapping(target = "deleted", ignore = true) // Assuming deleted is managed separately
    void updateCohortFromDTO(CohortDTO cohortDTO, @MappingTarget Cohort cohort);
}