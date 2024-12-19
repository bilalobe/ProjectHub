package com.projecthub.core.mappers;

import com.projecthub.core.dto.SchoolDTO;
import com.projecthub.core.models.School;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SchoolMapper {

    School toSchool(SchoolDTO schoolDTO);

    SchoolDTO toSchoolDTO(School school);

    void updateSchoolFromDTO(SchoolDTO schoolDTO, @MappingTarget School school);

}