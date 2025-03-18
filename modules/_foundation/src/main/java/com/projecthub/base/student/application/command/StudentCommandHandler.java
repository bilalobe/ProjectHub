package com.projecthub.base.student.application.command;

import com.projecthub.base.student.api.dto.StudentDTO;
import com.projecthub.base.student.application.service.StudentCommandService;
import com.projecthub.base.student.application.service.StudentQueryService;
import com.projecthub.base.student.domain.command.CreateStudentCommand;
import com.projecthub.base.student.domain.command.DeleteStudentCommand;
import com.projecthub.base.student.domain.command.UpdateStudentCommand;
import com.projecthub.base.student.domain.entity.Student;
import com.projecthub.base.student.domain.validation.StudentValidator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class StudentCommandHandler {
    private final StudentCommandService commandService;
    private final StudentValidator validator;
    private final StudentQueryService queryService;

    public StudentCommandHandler(StudentCommandService commandService, StudentValidator validator, StudentQueryService queryService) {
        this.commandService = commandService;
        this.validator = validator;
        this.queryService = queryService;
    }

    public StudentDTO handle(CreateStudentCommand command) {
        Student student = Student.builder()
            .firstName(command.firstName())
            .lastName(command.lastName())
            .middleName(command.middleName())
            .email(command.email())
            .phoneNumber(command.phoneNumber())
            .emergencyContact(command.emergencyContact())
            .build();

        validator.validateCreate(student);
        return commandService.createStudent(command);
    }

    public StudentDTO handle(UpdateStudentCommand command) {
        Student student = queryService.findById(command.id());
        validator.validateUpdate(student, command.id());
        return commandService.updateStudent(command);
    }

    public void handle(DeleteStudentCommand command) {
        commandService.deleteStudent(command);
    }
}
