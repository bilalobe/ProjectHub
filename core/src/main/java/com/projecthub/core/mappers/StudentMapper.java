package com.projecthub.core.mappers;

import com.projecthub.core.dto.StudentDTO;
import com.projecthub.core.models.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "team.id", source = "teamId")
    Student toStudent(StudentDTO studentDTO);

    @Mapping(source = "team.id", target = "teamId")
    StudentDTO toStudentDTO(Student student);

}