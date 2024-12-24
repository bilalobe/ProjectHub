package com.projecthub.core.controllers;

import com.projecthub.core.api.StudentApi;
import com.projecthub.core.dto.StudentDTO;
import com.projecthub.core.services.student.StudentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController implements StudentApi {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Override
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        logger.info("Retrieving all students");
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @Override
    public ResponseEntity<StudentDTO> getById(UUID id) {
        logger.info("Retrieving student with ID {}", id);
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @Override
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody StudentDTO student) {
        logger.info("Creating new student");
        return ResponseEntity.ok(studentService.saveStudent(student));
    }

    @Override
    public ResponseEntity<StudentDTO> updateStudent(UUID id, @Valid @RequestBody StudentDTO student) {
        logger.info("Updating student with ID {}", id);
        return ResponseEntity.ok(studentService.updateStudent(student));
    }

    @Override
    public ResponseEntity<Void> deleteById(UUID id) {
        logger.info("Deleting student with ID {}", id);
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}