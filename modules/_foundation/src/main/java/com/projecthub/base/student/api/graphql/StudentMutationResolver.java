package com.projecthub.base.student.api.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import com.projecthub.base.student.api.dto.StudentDTO;
import com.projecthub.base.student.api.graphql.input.CreateStudentInput;
import com.projecthub.base.student.api.graphql.input.UpdateStudentInput;
import com.projecthub.base.student.application.service.StudentCommandService;
import com.projecthub.base.student.domain.command.CreateStudentCommand;
import com.projecthub.base.student.domain.command.DeleteStudentCommand;
import com.projecthub.base.student.domain.command.UpdateStudentCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;

import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
@Slf4j
public class StudentMutationResolver {
    private final StudentCommandService commandService;

    @DgsMutation
    public StudentDTO createStudent(@InputArgument CreateStudentInput input, Authentication authentication) {
        log.debug("GraphQL mutation: Creating student {}", input);
        CreateStudentCommand command = new CreateStudentCommand(
            input.getFirstName(),
            input.getLastName(),
            input.getMiddleName(),
            input.getEmail(),
            input.getPhoneNumber(),
            input.getEmergencyContact(),
            input.getTeamId(),
            UUID.randomUUID() // TODO: Get from authentication context
        );
        return commandService.createStudent(command);
    }

    @DgsMutation
    public StudentDTO updateStudent(
        @InputArgument String id,
        @InputArgument UpdateStudentInput input,
        Authentication authentication) {
        log.debug("GraphQL mutation: Updating student {}", id);
        UpdateStudentCommand command = new UpdateStudentCommand(
            UUID.fromString(id),
            input.getFirstName(),
            input.getLastName(),
            input.getMiddleName(),
            input.getEmail(),
            input.getPhoneNumber(),
            input.getEmergencyContact(),
            input.getTeamId(),
            UUID.randomUUID() // TODO: Get from authentication context
        );
        return commandService.updateStudent(command);
    }

    @DgsMutation
    public Boolean deleteStudent(@InputArgument String id, Authentication authentication) {
        log.debug("GraphQL mutation: Deleting student {}", id);
        DeleteStudentCommand command = new DeleteStudentCommand(
            UUID.fromString(id),
            UUID.randomUUID(), // TODO: Get from authentication context
            null
        );
        commandService.deleteStudent(command);
        return Boolean.TRUE;
    }
}
