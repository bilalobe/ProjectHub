package com.projecthub.mapper;

import com.projecthub.dto.SchoolDTO;
import com.projecthub.model.School;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SchoolMapper {

    School toSchool(SchoolDTO schoolDTO);

    SchoolDTO toSchoolDTO(School school);

    void updateSchoolFromDTO(SchoolDTO schoolDTO, @MappingTarget School school);

}