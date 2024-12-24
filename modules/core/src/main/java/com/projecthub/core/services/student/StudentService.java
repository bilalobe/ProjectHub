package com.projecthub.core.services.student;

import com.projecthub.core.dto.StudentDTO;
import com.projecthub.core.exceptions.ResourceNotFoundException;
import com.projecthub.core.mappers.StudentMapper;
import com.projecthub.core.models.Student;
import com.projecthub.core.repositories.jpa.StudentJpaRepository;

import jakarta.validation.Valid;

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

    private static final String STUDENT_NOT_FOUND = "Student not found with ID: ";

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
        Student student = studentMapper.toEntity(studentDTO);
        Student savedStudent = studentRepository.save(student);
        logger.info("Student created with ID {}", savedStudent.getId());
        return studentMapper.toDto(savedStudent);
    }

    /**
     * Deletes a student by ID.
     *
     * @param id the ID of the student to delete
     * @throws ResourceNotFoundException if the student is not found
     */
    @Transactional
    public void deleteStudent(UUID id) {
            throw new ResourceNotFoundException(STUDENT_NOT_FOUND + id);
    }

    /**
     * Retrieves a student by ID.
     *
     * @param id the ID of the student to retrieve
     * @return the student DTO
     * @throws ResourceNotFoundException if the student is not found
     */
    public StudentDTO getStudentById(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STUDENT_NOT_FOUND + id));
        return studentMapper.toDto(student);
    }

    /**
     * Retrieves all students.
     *
     * @return a list of student DTOs
     */
    public List<StudentDTO> getAllStudents() {
        logger.info("Retrieving all students");
        return studentRepository.findAll().stream()
                .map(studentMapper::toDto)
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

    /**
     * Updates an existing student.
     *
     * @param studentDTO the student data transfer object with updated information
     * @return the updated student DTO
     * @throws ResourceNotFoundException if the student is not found
     * @throws IllegalArgumentException if studentDTO is null or ID is null
     */
    @Transactional
    public StudentDTO updateStudent(@Valid StudentDTO studentDTO) {
        logger.info("Updating student with ID {}", studentDTO.id());
        
        if (studentDTO.id() == null) {
            throw new IllegalArgumentException("Student ID cannot be null for update");
        }
        
        validateStudentDTO(studentDTO);
            Student existingStudent = studentRepository.findById(studentDTO.id())
                .orElseThrow(() -> new ResourceNotFoundException(
                    STUDENT_NOT_FOUND + studentDTO.id()));

        // Update fields
        studentMapper.updateEntityFromDto(studentDTO, existingStudent);
        
        Student savedStudent = studentRepository.save(existingStudent);
        logger.info("Student updated successfully with ID {}", savedStudent.getId());
        
        return studentMapper.toDto(savedStudent);
    }
}