package com.projecthub.base.student.application.port.in;

import com.projecthub.base.student.api.dto.StudentDTO;
import com.projecthub.base.student.domain.command.CreateStudentCommand;

public interface CreateStudentUseCase {
    StudentDTO createStudent(CreateStudentCommand command);
}