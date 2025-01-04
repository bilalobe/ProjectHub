package com.projecthub.base.school.api.mapper;

import com.projecthub.base.school.api.dto.SchoolAddressDTO;
import com.projecthub.base.school.api.dto.SchoolContactDTO;
import com.projecthub.base.school.api.dto.SchoolDTO;
import com.projecthub.base.school.api.dto.SchoolIdentifierDTO;
import com.projecthub.base.school.domain.entity.School;
import com.projecthub.base.school.domain.value.SchoolAddress;
import com.projecthub.base.school.domain.value.SchoolContact;
import com.projecthub.base.school.domain.value.SchoolIdentifier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SchoolMapper {
    @Mapping(target = "identifier.formatted", expression = "java(school.getIdentifier().formatted())")
    SchoolDTO toDto(School school);

    School toEntity(SchoolDTO dto);

    SchoolAddressDTO toDto(SchoolAddress address);

    SchoolAddress toEntity(SchoolAddressDTO dto);

    SchoolContactDTO toDto(SchoolContact contact);

    SchoolContact toEntity(SchoolContactDTO dto);

    SchoolIdentifierDTO toDto(SchoolIdentifier identifier);

    SchoolIdentifier toEntity(SchoolIdentifierDTO dto);
}
