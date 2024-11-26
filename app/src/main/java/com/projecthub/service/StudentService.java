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
import com.projecthub.repository.custom.CustomStudentRepository;
import com.projecthub.repository.custom.CustomTeamRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Service
@Tag(name = "Student Service", description = "Operations pertaining to students in ProjectHub")
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final CustomStudentRepository studentRepository;

    public StudentService(@Qualifier("csvStudentRepository") CustomStudentRepository studentRepository,
                          @Qualifier("csvTeamRepository") CustomTeamRepository teamRepository,
                          StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
    }

    /**
     * Retrieves a list of all student summaries.
     *
     * @return a list of StudentSummary
     */
    @Operation(summary = "View a list of all student summaries")
    public List<StudentSummary> getAllStudentSummaries() {
        logger.info("Retrieving all student summaries");
        return studentRepository.findAll().stream()
                .map(StudentMapper::toStudentSummary)
                .collect(Collectors.toList());
    }

    /**
     * Saves a student.
     *
     * @param studentSummary the student summary to save
     * @throws IllegalArgumentException if studentSummary is null
     */
    @Operation(summary = "Save a student")
    @Transactional
    public void saveStudent(StudentSummary studentSummary) {
        logger.info("Saving student");
        if (studentSummary == null) {
            throw new IllegalArgumentException("StudentSummary cannot be null");
        }
        Student student = StudentMapper.toStudent(studentSummary);
        studentRepository.save(student);
        logger.info("Student saved");
    }

    /**
     * Deletes a student by ID.
     *
     * @param id the ID of the student to delete
     * @throws IllegalArgumentException if id is null
     * @throws ResourceNotFoundException if the student is not found
     */
    @Operation(summary = "Delete a student by ID")
    @Transactional
    public void deleteStudent(Long id) {
        logger.info("Deleting student with ID: {}", id);
        if (id == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
        studentRepository.delete(student);
        logger.info("Student deleted");
    }

    /**
     * Retrieves a student summary by ID.
     *
     * @param id the ID of the student to retrieve
     * @return StudentSummary
     * @throws IllegalArgumentException if id is null
     * @throws ResourceNotFoundException if the student is not found
     */
    @Operation(summary = "Retrieve a student summary by ID")
    public StudentSummary getStudentSummaryById(Long id) throws ResourceNotFoundException {
        logger.info("Retrieving student summary with ID {}", id);
        if (id == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }
        return studentRepository.findById(id)
                .map(StudentMapper::toStudentSummary)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));
    }
}