package com.projecthub.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projecthub.dto.StudentSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.service.StudentService;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Student API", description = "Operations pertaining to students in ProjectHub")
@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * Retrieves a list of all student summaries.
     *
     * @return a list of all student summaries
     */
    @GetMapping
    public List<StudentSummary> getAllStudents() {
        return studentService.getAllStudentSummaries();
    }

    /**
     * Retrieves a student summary by ID.
     *
     * @param id the ID of the student to retrieve
     * @return a StudentSummary object
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentSummary> getStudentById(@PathVariable Long id) {
        try {
            StudentSummary student = studentService.getStudentSummaryById(id);
            return ResponseEntity.ok(student);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    /**
     * Saves a student.
     *
     * @param studentSummary the student summary to save
     * @return a message indicating the result
     */
    @Operation(summary = "Create a new student")
    @PostMapping
    public ResponseEntity<String> createStudent(@Valid @RequestBody StudentSummary studentSummary) {
        try {
            studentService.saveStudent(studentSummary);
            return ResponseEntity.ok("Student created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error creating student");
        }
    }

    /**
     * Deletes a student by ID.
     *
     * @param id the ID of the student to delete
     * @return a message indicating the result
     */
    @Operation(summary = "Delete a student by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.ok("Student deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }
}