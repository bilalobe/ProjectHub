package com.projecthub.base.student.application.port.in;

import com.projecthub.base.student.domain.command.DeleteStudentCommand;

public interface DeleteStudentUseCase {
    void deleteStudent(DeleteStudentCommand command);
}