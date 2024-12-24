package com.projecthub.core.mappers;

import com.projecthub.core.dto.SchoolDTO;
import com.projecthub.core.models.School;
import org.mapstruct.*;

/**
 * Mapper for School entity with protected field handling.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SchoolMapper extends BaseMapper<SchoolDTO, School> {

    @Override
    @Mapping(target = "id", ignore = true)
    School toEntity(SchoolDTO dto);

    @Override
    SchoolDTO toDto(School entity);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(SchoolDTO dto, @MappingTarget School entity);
}