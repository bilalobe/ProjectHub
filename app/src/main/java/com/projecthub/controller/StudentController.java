package com.projecthub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.projecthub.dto.StudentSummary;
import com.projecthub.service.StudentService;

import jakarta.validation.Valid;

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
    public StudentSummary getStudentById(@PathVariable Long id) {
        return studentService.getStudentSummaryById(id);
    }

    /**
     * Saves a student.
     *
     * @param studentSummary the student summary to save
     * @return a message indicating the result
     */
    @PostMapping
    public String createStudent(@Valid @RequestBody StudentSummary studentSummary) {
        studentService.saveStudent(studentSummary);
        return "Student created successfully";
    }

    /**
     * Deletes a student by ID.
     *
     * @param id the ID of the student to delete
     */
    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }
}