package com.projecthub.mapper;

import org.springframework.stereotype.Component;

import com.projecthub.dto.StudentSummary;
import com.projecthub.model.Student;

@Component
public class StudentMapper {

    public StudentSummary toStudentSummary(Student student) {
        if (student == null) {
            return null;
        }
        return new StudentSummary(
            student.getId(),
            student.getUsername(),
            student.getEmail(),
            student.getFirstName(),
            student.getLastName(),
            student.getTeam() != null ? student.getTeam().getName() : null
        );
    }

    public Student toStudent(StudentSummary studentSummary) {
        if (studentSummary == null) {
            return null;
        }
        Student student = new Student();
        student.setId(studentSummary.getId());
        student.setUsername(studentSummary.getUsername());
        student.setEmail(studentSummary.getEmail());
        student.setFirstName(studentSummary.getFirstName());
        student.setLastName(studentSummary.getLastName());
        // Additional mappings can be added here
        return student;
    }

    public void updateStudentFromSummary(StudentSummary studentSummary, Student student) {
        if (studentSummary == null || student == null) {
            return;
        }
        student.setUsername(studentSummary.getUsername());
        student.setEmail(studentSummary.getEmail());
        student.setFirstName(studentSummary.getFirstName());
        student.setLastName(studentSummary.getLastName());
        // Additional mappings can be added here
    }
}