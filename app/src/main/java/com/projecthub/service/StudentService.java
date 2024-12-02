package com.projecthub.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projecthub.dto.StudentSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.mapper.StudentMapper;
import com.projecthub.model.Student;
import com.projecthub.repository.StudentRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Service
@Tag(name = "Student Service", description = "Operations pertaining to students in ProjectHub")
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;


    public StudentService(
            @Qualifier("studentRepository") StudentRepository studentRepository,
            StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
    }

    /**
     * Retrieves all students.
     *
     * @return a list of StudentSummary objects
     */
    @Operation(summary = "View a list of all students")
    public List<StudentSummary> getAllStudents() {
        logger.info("Retrieving all students");
        return studentRepository.findAll().stream()
                .map(studentMapper::toStudentSummary)
                .collect(Collectors.toList());
    }

    /**
     * Registers a new student.
     *
     * @param studentSummary the student summary to register
     * @return the registered StudentSummary
     * @throws IllegalArgumentException if studentSummary is null
     */
    @Operation(summary = "Register a new student")
    @Transactional
    public StudentSummary registerStudent(StudentSummary studentSummary) {
        logger.info("Registering new student: {}", studentSummary.getUsername());

        Student student = studentMapper.toStudent(studentSummary);
        Student savedStudent = studentRepository.save(student);
        logger.info("Student registered with ID {}", savedStudent.getId());
        return studentMapper.toStudentSummary(savedStudent);
    }

    /**
     * Removes a student by ID.
     *
     * @param studentId the ID of the student to remove
     * @throws IllegalArgumentException    if studentId is null
     * @throws ResourceNotFoundException if the student is not found
     */
    @Operation(summary = "Remove a student by ID")
    @Transactional
    public void removeStudentById(Long studentId) {
        logger.info("Removing student with ID: {}", studentId);
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
        studentRepository.deleteById(studentId);
        logger.info("Student removed with ID: {}", studentId);
    }

    /**
     * Retrieves a student by ID.
     *
     * @param studentId the ID of the student to retrieve
     * @return the StudentSummary
     * @throws IllegalArgumentException    if studentId is null
     * @throws ResourceNotFoundException if the student is not found
     */
    @Operation(summary = "Retrieve a student by ID")
    public StudentSummary getStudentById(Long studentId) {
        logger.info("Retrieving student with ID {}", studentId);
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }
        return studentRepository.findById(studentId)
                .map(studentMapper::toStudentSummary)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
    }

    /**
     * Retrieves a student by username.
     *
     * @param username the username of the student to retrieve
     * @return the StudentSummary
     * @throws IllegalArgumentException    if username is null or empty
     * @throws ResourceNotFoundException if the student is not found
     */
    @Operation(summary = "Retrieve a student by username")
    public StudentSummary getStudentByUsername(String username) {
        logger.info("Retrieving student with username {}", username);
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        return studentRepository.findByUsername(username)
                .map(studentMapper::toStudentSummary)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with username: " + username));
    }

    /**
     * Updates a student's information.
     *
     * @param studentId the ID of the student to update
     * @param studentSummary the updated student summary
     * @return the updated StudentSummary
     * @throws IllegalArgumentException if studentId or studentSummary is null
     * @throws ResourceNotFoundException if the student is not found
     */
    @Operation(summary = "Update a student's information")
    @Transactional
    public StudentSummary updateStudent(Long studentId, StudentSummary studentSummary) {
        logger.info("Updating student with ID: {}", studentId);
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }
        if (studentSummary == null) {
            throw new IllegalArgumentException("StudentSummary cannot be null");
        }
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
        studentMapper.updateStudentFromSummary(studentSummary, student);
        Student updatedStudent = studentRepository.save(student);
        logger.info("Student updated with ID: {}", updatedStudent.getId());
        return studentMapper.toStudentSummary(updatedStudent);
    }
}