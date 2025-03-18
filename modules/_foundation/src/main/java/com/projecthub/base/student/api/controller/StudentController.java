package com.projecthub.base.student.api.controller;

import com.projecthub.base.shared.domain.enums.status.ActivationStatus;
import com.projecthub.base.student.api.dto.StudentDTO;
import com.projecthub.base.student.api.dto.StudentSearchCriteria;
import com.projecthub.base.student.application.service.StudentCommandService;
import com.projecthub.base.student.application.service.StudentQueryService;
import com.projecthub.base.student.domain.command.CreateStudentCommand;
import com.projecthub.base.student.domain.command.DeleteStudentCommand;
import com.projecthub.base.student.domain.command.UpdateStudentCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/students")
@Tag(name = "Student", description = "Student management APIs")
@RequiredArgsConstructor
public class StudentController {
    private final StudentCommandService commandService;
    private final StudentQueryService queryService;

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        return ResponseEntity.ok(queryService.getAllStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(queryService.getById(id));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<StudentDTO>> getStudentsByTeamId(@PathVariable UUID teamId) {
        return ResponseEntity.ok(queryService.getByTeamId(teamId));
    }

    @PostMapping
    @Operation(summary = "Create a new student")
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody CreateStudentCommand command) {
        return ResponseEntity.ok(commandService.createStudent(command));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing student")
    public ResponseEntity<StudentDTO> updateStudent(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStudentCommand command) {
        return ResponseEntity.ok(commandService.updateStudent(command));
    }

    @PutMapping("/{studentId}/team/{teamId}")
    public ResponseEntity<StudentDTO> assignToTeam(@PathVariable UUID studentId, @PathVariable UUID teamId) {
        StudentDTO student = queryService.getById(studentId);
        UpdateStudentCommand command = new UpdateStudentCommand(
            studentId,
            student.firstName(),
            student.lastName(),
            student.middleName(),
            student.email(),
            student.phoneNumber(),
            student.emergencyContact(),
            teamId,
            UUID.randomUUID()
        );
        return ResponseEntity.ok(commandService.updateStudent(command));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        commandService.deleteStudent(new DeleteStudentCommand(id, UUID.randomUUID(), null));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search students with criteria")
    public ResponseEntity<Page<StudentDTO>> searchStudents(
        @Parameter(description = "Search term for name")
        @RequestParam(required = false) String nameSearch,

        @Parameter(description = "Filter by email")
        @RequestParam(required = false) String email,

        @Parameter(description = "Filter by team ID")
        @RequestParam(required = false) UUID teamId,

        @Parameter(description = "Filter by student status (ACTIVE, INACTIVE, PENDING)")
        @RequestParam(required = false) ActivationStatus status,

        @Parameter(description = "Filter by enrollment date start")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate enrollmentDateStart,

        @Parameter(description = "Filter by enrollment date end")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate enrollmentDateEnd,

        @Parameter(description = "Pagination parameters")
        Pageable pageable
    ) {
        StudentSearchCriteria criteria = StudentSearchCriteria.builder()
            .nameSearch(nameSearch)
            .email(email)
            .teamId(teamId)
            .status(status)
            .enrollmentDateStart(enrollmentDateStart)
            .enrollmentDateEnd(enrollmentDateEnd)
            .build();

        return ResponseEntity.ok(queryService.searchStudents(criteria, pageable));
    }
}
