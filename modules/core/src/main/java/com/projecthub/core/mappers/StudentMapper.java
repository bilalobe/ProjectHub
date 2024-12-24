package com.projecthub.core.mappers;

import com.projecthub.core.dto.StudentDTO;
import com.projecthub.core.models.Student;
import org.mapstruct.*;

/**
 * Mapper for Student entity with protected relationship handling.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper extends BaseMapper<StudentDTO, Student> {

    @Override
    @Mapping(target = "team.id", source = "teamId")
    @Mapping(target = "id", ignore = true)
    Student toEntity(StudentDTO dto);

    @Override
    @Mapping(source = "team.id", target = "teamId")
    StudentDTO toDto(Student entity);

    @Override
    @Mapping(target = "team.id", source = "teamId")
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(StudentDTO dto, @MappingTarget Student entity);
}