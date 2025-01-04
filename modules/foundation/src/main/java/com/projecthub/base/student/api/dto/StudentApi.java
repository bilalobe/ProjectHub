package com.projecthub.base.student.api.dto;

import com.projecthub.base.shared.api.rest.BaseApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Students", description = "Student management API")
@RequestMapping("/api/v1/students")
public interface StudentApi extends BaseApi<StudentDTO, UUID> {

    @Operation(summary = "Get all students")
    @GetMapping
    ResponseEntity<List<StudentDTO>> getAllStudents();

    @Operation(summary = "Create student")
    @PostMapping
    ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody StudentDTO studentDTO);

    @Operation(summary = "Update student")
    @PutMapping("/{id}")
    ResponseEntity<StudentDTO> updateStudent(
        @PathVariable UUID id,
        @Valid @RequestBody StudentDTO studentDTO);
}
