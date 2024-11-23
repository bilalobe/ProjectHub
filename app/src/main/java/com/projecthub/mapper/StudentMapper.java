package com.projecthub.mapper;

import com.projecthub.dto.StudentSummary;
import com.projecthub.model.Student;
import com.projecthub.model.Team;

public class StudentMapper {

    public static Student toStudent(StudentSummary studentSummary) {
        Student student = new Student();
        student.setId(studentSummary.getId());
        student.setName(studentSummary.getName());
        Team team = new Team();
        team.setId(studentSummary.getTeamId());
        student.setTeam(team);
        return student;
    }

    public static StudentSummary toStudentSummary(Student student) {
        return new StudentSummary(
            student.getId(),
            student.getName(),
            student.getTeam() != null ? student.getTeam().getId() : null
        );
    }
}