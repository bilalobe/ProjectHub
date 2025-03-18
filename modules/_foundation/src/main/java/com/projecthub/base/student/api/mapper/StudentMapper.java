package com.projecthub.base.student.api.mapper;

import com.projecthub.base.shared.api.mapper.BaseMapper;
import com.projecthub.base.student.api.dto.StudentDTO;
import com.projecthub.base.student.domain.entity.Student;
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
    @Mapping(target = "teamId", source = "team.id")
    @Mapping(target = "teamName", source = "team.name")
    StudentDTO toDto(Student entity);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(StudentDTO dto, @MappingTarget Student entity);
}
