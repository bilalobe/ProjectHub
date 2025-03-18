package com.projecthub.base.student.application.service;

import com.projecthub.base.shared.exception.ResourceNotFoundException;
import com.projecthub.base.student.api.dto.StudentDTO;
import com.projecthub.base.student.api.mapper.StudentMapper;
import com.projecthub.base.student.domain.aggregate.StudentAggregate;
import com.projecthub.base.student.domain.command.CreateStudentCommand;
import com.projecthub.base.student.domain.command.DeleteStudentCommand;
import com.projecthub.base.student.domain.command.UpdateStudentCommand;
import com.projecthub.base.student.domain.entity.Student;
import com.projecthub.base.student.domain.event.StudentDomainEvent;
import com.projecthub.base.student.domain.event.StudentEventPublisher;
import com.projecthub.base.student.domain.repository.StudentJpaRepository;
import com.projecthub.base.team.domain.entity.Team;
import com.projecthub.base.team.domain.repository.TeamJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StudentCommandService {
    private final StudentJpaRepository studentRepository;
    private final TeamJpaRepository teamRepository;
    private final StudentMapper studentMapper;
    private final StudentEventPublisher eventPublisher;

    public StudentDTO createStudent(CreateStudentCommand command) {
        log.debug("Creating student from command: {}", command);

        Team team = command.teamId() != null ? teamRepository.findById(command.teamId())
            .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + command.teamId()))
            : null;

        Student student = Student.builder()
            .firstName(command.firstName())
            .lastName(command.lastName())
            .middleName(command.middleName())
            .email(command.email())
            .phoneNumber(command.phoneNumber())
            .emergencyContact(command.emergencyContact())
            .team(team)
            .enrollmentDate(LocalDate.now())
            .build();

        StudentAggregate aggregate = StudentAggregate.reconstitute(student, command.initiatorId());
        Student savedStudent = studentRepository.save(aggregate.getRoot());
        
        // Publish all events generated during creation
        aggregate.getDomainEvents().forEach(eventPublisher::publish);
        aggregate.clearDomainEvents();

        log.info("Student created with ID: {}", savedStudent.getStudentId());
        return studentMapper.toDto(savedStudent);
    }

    public StudentDTO updateStudent(UpdateStudentCommand command) {
        log.debug("Updating student from command: {}", command);

        Student student = studentRepository.findById(command.id())
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + command.id()));

        Team team = command.teamId() != null ? teamRepository.findById(command.teamId())
            .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + command.teamId()))
            : student.getTeam();

        StudentAggregate aggregate = StudentAggregate.reconstitute(student, command.initiatorId());
        
        aggregate.updateDetails(
            command.firstName(),
            command.lastName(),
            command.middleName(),
            command.email()
        );

        aggregate.updateContactInfo(
            command.phoneNumber(),
            command.emergencyContact()
        );

        if (team != student.getTeam()) {
            aggregate.assignToTeam(team);
        }

        Student savedStudent = studentRepository.save(aggregate.getRoot());
        
        // Publish all events generated during update
        aggregate.getDomainEvents().forEach(eventPublisher::publish);
        aggregate.clearDomainEvents();

        log.info("Student updated with ID: {}", savedStudent.getStudentId());
        return studentMapper.toDto(savedStudent);
    }

    public void deleteStudent(DeleteStudentCommand command) {
        log.debug("Deleting student with ID: {}", command.id());

        Student student = studentRepository.findById(command.id())
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + command.id()));
            
        StudentAggregate aggregate = StudentAggregate.reconstitute(student, command.initiatorId());
        studentRepository.delete(student);
        
        // Publish delete event
        eventPublisher.publish(new StudentDomainEvent.Deleted(
            UUID.randomUUID(),
            student.getStudentId(),
            command.initiatorId(),
            Instant.now()
        ));

        log.info("Student deleted with ID: {}", command.id());
    }
}
