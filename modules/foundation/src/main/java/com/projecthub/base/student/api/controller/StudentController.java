package com.projecthub.base.student.api.controller;

import com.projecthub.base.student.api.dto.StudentApi;
import com.projecthub.base.student.api.dto.StudentDTO;
import com.projecthub.base.student.application.service.StudentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController implements StudentApi {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
    private final StudentService studentService;

    public StudentController(final StudentService studentService) {
        this.studentService = studentService;
    }

    @Override
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        StudentController.logger.info("Retrieving all students");
        return ResponseEntity.ok(this.studentService.getAllStudents());
    }

    @Override
    public ResponseEntity<StudentDTO> getById(final UUID id) {
        StudentController.logger.info("Retrieving student with ID {}", id);
        return ResponseEntity.ok(this.studentService.getStudentById(id));
    }

    @Override
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody final StudentDTO student) {
        StudentController.logger.info("Creating new student");
        return ResponseEntity.ok(this.studentService.saveStudent(student));
    }

    @Override
    public ResponseEntity<StudentDTO> updateStudent(final UUID id, @Valid @RequestBody final StudentDTO student) {
        StudentController.logger.info("Updating student with ID {}", id);
        return ResponseEntity.ok(this.studentService.updateStudent(student));
    }

    @Override
    public ResponseEntity<Void> deleteById(final UUID id) {
        StudentController.logger.info("Deleting student with ID {}", id);
        this.studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
