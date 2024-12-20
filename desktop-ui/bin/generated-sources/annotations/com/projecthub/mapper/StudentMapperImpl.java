package com.projecthub.mapper;

import com.projecthub.dto.StudentDTO;
import com.projecthub.model.Student;
import com.projecthub.model.Team;

import java.util.UUID;
import javax.annotation.processing.Generated;

import org.springframework.stereotype.Component;

@Generated(
        value = "org.mapstruct.ap.MappingProcessor",
        date = "2024-12-08T14:18:24+0100",
        comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.40.0.z20241112-1021, environment: Java 17.0.13 (Eclipse Adoptium)"
)
@Component
public class StudentMapperImpl extends StudentMapper {

    @Override
    public Student toStudent(StudentDTO studentDTO) {
        if (studentDTO == null) {
            return null;
        }

        Student student = new Student();

        student.setTeam(mapTeamIdToTeam(studentDTO.getTeamId()));
        student.setId(studentDTO.getId());
        student.setEmail(studentDTO.getEmail());
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());

        return student;
    }

    @Override
    public StudentDTO toStudentDTO(Student student) {
        if (student == null) {
            return null;
        }

        StudentDTO studentDTO = new StudentDTO();

        studentDTO.setTeamId(studentTeamId(student));
        studentDTO.setId(student.getId());
        studentDTO.setEmail(student.getEmail());
        studentDTO.setFirstName(student.getFirstName());
        studentDTO.setLastName(student.getLastName());

        return studentDTO;
    }

    @Override
    public void updateStudentFromDTO(StudentDTO studentDTO, Student student) {
        if (studentDTO == null) {
            return;
        }

        student.setTeam(mapTeamIdToTeam(studentDTO.getTeamId()));
        student.setId(studentDTO.getId());
        student.setEmail(studentDTO.getEmail());
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
    }

    private UUID studentTeamId(Student student) {
        Team team = student.getTeam();
        if (team == null) {
            return null;
        }
        return team.getId();
    }
}
