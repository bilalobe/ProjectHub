package com.projecthub.core.services;

import com.projecthub.core.dto.StudentDTO;
import com.projecthub.core.mappers.StudentMapper;
import com.projecthub.core.models.Student;
import com.projecthub.core.repositories.jpa.StudentJpaRepository;
import com.projecthub.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing students.
 */
@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentJpaRepository studentRepository;
    private final StudentMapper studentMapper;

    public StudentService(StudentJpaRepository studentRepository, StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
    }

    /**
     * Creates a new student.
     *
     * @param studentDTO the student data transfer object
     * @return the created student DTO
     * @throws IllegalArgumentException if studentDTO is null
     */
    @Transactional
    public StudentDTO saveStudent(StudentDTO studentDTO) {
        logger.info("Creating a new student");
        validateStudentDTO(studentDTO);
        Student student = studentMapper.toStudent(studentDTO);
        Student savedStudent = studentRepository.save(student);
        logger.info("Student created with ID {}", savedStudent.getId());
        return studentMapper.toStudentDTO(savedStudent);
    }

    /**
     * Deletes a student by ID.
     *
     * @param id the ID of the student to delete
     * @throws ResourceNotFoundException if the student is not found
     */
    @Transactional
    public void deleteStudent(UUID id) {
        logger.info("Deleting student with ID {}", id);
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with ID: " + id);
        }
        studentRepository.deleteById(id);
        logger.info("Student deleted with ID {}", id);
    }

    /**
     * Retrieves a student by ID.
     *
     * @param id the ID of the student to retrieve
     * @return the student DTO
     * @throws ResourceNotFoundException if the student is not found
     */
    public StudentDTO getStudentById(UUID id) {
        logger.info("Retrieving student with ID {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
        return studentMapper.toStudentDTO(student);
    }

    /**
     * Retrieves all students.
     *
     * @return a list of student DTOs
     */
    public List<StudentDTO> getAllStudents() {
        logger.info("Retrieving all students");
        return studentRepository.findAll().stream()
                .map(studentMapper::toStudentDTO)
                .toList();
    }

    /**
     * Validates the student DTO.
     *
     * @param studentDTO the student data transfer object
     * @throws IllegalArgumentException if studentDTO is null
     */
    private void validateStudentDTO(StudentDTO studentDTO) {
        if (studentDTO == null) {
            throw new IllegalArgumentException("StudentDTO cannot be null");
        }
        // Additional validation logic can be added here
    }
}