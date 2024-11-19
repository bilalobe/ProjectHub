package com.projecthub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.projecthub.dto.StudentSummary;
import com.projecthub.model.Student;
import com.projecthub.service.StudentService;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * Retrieves a list of all students.
     *
     * @return a list of all students
     */
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    /**
     * Retrieves a list of all student summaries.
     *
     * @return a list of all student summaries
     */
    @GetMapping("/summaries")
    public List<StudentSummary> getAllStudentSummaries() {
        return studentService.getAllStudentSummaries();
    }

    /**
     * Retrieves a student by ID.
     *
     * @param id the ID of the student to retrieve
     * @return a Student object
     */
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id " + id));
    }

    /**
     * Retrieves a student summary by ID.
     *
     * @param id the ID of the student to retrieve
     * @return a StudentSummary object
     */
    @GetMapping("/summaries/{id}")
    public StudentSummary getStudentSummaryById(@PathVariable Long id) {
        return studentService.getStudentSummaryById(id);
    }

    /**
     * Saves a student.
     *
     * @param student the student to save
     * @return the saved student
     */
    @PostMapping
    public Student saveStudent(@RequestBody Student student) {
        return studentService.saveStudent(student);
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