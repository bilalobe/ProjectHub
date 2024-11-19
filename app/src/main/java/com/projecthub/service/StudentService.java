package com.projecthub.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.projecthub.dto.StudentSummary;
import com.projecthub.model.Student;
import com.projecthub.repository.custom.CustomStudentRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Service
@Tag(name = "Student Service", description = "Operations pertaining to students in ProjectHub")
public class StudentService {

    private final CustomStudentRepository studentRepository;

    public StudentService(@Qualifier("csvStudentRepository") CustomStudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Retrieves a list of all students.
     *
     * @return a list of all students
     */
    @Operation(summary = "View a list of all students")
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * Retrieves a list of all student summaries.
     *
     * @return a list of all student summaries
     */
    @Operation(summary = "View a list of all student summaries")
    public List<StudentSummary> getAllStudentSummaries() {
        return studentRepository.findAll().stream()
                .map(StudentSummary::new)
                .collect(Collectors.toList());
    }

    /**
     * Saves a student.
     *
     * @param student the student to save
     * @return the saved student
     */
    @Operation(summary = "Save a student")
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    /**
     * Deletes a student by ID.
     *
     * @param id the ID of the student to delete
     */
    @Operation(summary = "Delete a student by ID")
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    /**
     * Retrieves a student by ID.
     *
     * @param id the ID of the student to retrieve
     * @return an Optional containing the student if found, or empty if not found
     */
    @Operation(summary = "Retrieve a student by ID")
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    /**
     * Retrieves a student summary by ID.
     *
     * @param id the ID of the student to retrieve
     * @return a StudentSummary object
     */
    @Operation(summary = "Retrieve a student summary by ID")
    public StudentSummary getStudentSummaryById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id " + id));
        return new StudentSummary(student);
    }
}