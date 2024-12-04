package com.projecthub.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projecthub.dto.StudentDTO;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.service.StudentService;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name = "Student API", description = "Operations pertaining to students in ProjectHub")
@RestController
@RequestMapping("/students")
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Retrieves a list of all student summaries.
     *
     * @return a list of all student summaries
     */
    @Operation(summary = "Get all students")
    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        logger.info("Retrieving all students");
        List<StudentDTO> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    /**
     * Retrieves a student summary by ID.
     *
     * @param id the ID of the student to retrieve
     * @return a StudentDTO object
     */
    @Operation(summary = "Get student by ID")
    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable UUID id) {
        logger.info("Retrieving student with ID {}", id);
        StudentDTO student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    /**
     * Creates a new student.
     *
     * @param studentDTO the student DTO to create
     * @return a message indicating the result
     */
    @Operation(summary = "Create a new student")
    @PostMapping
    public ResponseEntity<String> createStudent(@Valid @RequestBody StudentDTO studentDTO) {
        logger.info("Creating a new student");
        studentService.saveStudent(studentDTO);
        return ResponseEntity.ok("Student created successfully");
    }

    /**
     * Deletes a student by ID.
     *
     * @param id the ID of the student to delete
     * @return a message indicating the result
     */
    @Operation(summary = "Delete a student by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable UUID id) {
        logger.info("Deleting student with ID {}", id);
        studentService.deleteStudent(id);
        return ResponseEntity.ok("Student deleted successfully");
    }

    @ExceptionHandler({ResourceNotFoundException.class, Exception.class})
    public ResponseEntity<String> handleExceptions(Exception ex) {
        logger.error("An error occurred", ex);
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}