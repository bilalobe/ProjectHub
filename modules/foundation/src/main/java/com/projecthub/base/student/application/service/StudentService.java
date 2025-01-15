package com.projecthub.base.student.application.service;


import com.projecthub.base.shared.exception.ResourceNotFoundException;
import com.projecthub.base.student.api.dto.StudentDTO;
import com.projecthub.base.student.api.mapper.StudentMapper;
import com.projecthub.base.student.domain.entity.Student;
import com.projecthub.base.student.domain.repository.StudentJpaRepository;
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
    private static final String STUDENT_NOT_FOUND = "Student not found with ID: ";
    private final StudentJpaRepository studentRepository;
    private final StudentMapper studentMapper;

    public StudentService(final StudentJpaRepository studentRepository, final StudentMapper studentMapper) {
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
    public StudentDTO saveStudent(final StudentDTO studentDTO) {
        StudentService.logger.info("Creating a new student");
        this.validateStudentDTO(studentDTO);
        final Student student = this.studentMapper.toEntity(studentDTO);
        final Student savedStudent = this.studentRepository.save(student);
        StudentService.logger.info("Student created with ID {}", savedStudent.getId());
        return this.studentMapper.toDto(savedStudent);
    }

    /**
     * Deletes a student by ID.
     *
     * @param id the ID of the student to delete
     * @throws ResourceNotFoundException if the student is not found
     */
    @Transactional
    public void deleteStudent(final UUID id) {
        throw new ResourceNotFoundException(StudentService.STUDENT_NOT_FOUND + id);
    }

    /**
     * Retrieves a student by ID.
     *
     * @param id the ID of the student to retrieve
     * @return the student DTO
     * @throws ResourceNotFoundException if the student is not found
     */
    public StudentDTO getStudentById(final UUID id) {
        final Student student = this.studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(StudentService.STUDENT_NOT_FOUND + id));
        return this.studentMapper.toDto(student);
    }

    /**
     * Retrieves all students.
     *
     * @return a list of student DTOs
     */
    public List<StudentDTO> getAllStudents() {
        StudentService.logger.info("Retrieving all students");
        return this.studentRepository.findAll().stream()
            .map(this.studentMapper::toDto)
            .toList();
    }

    /**
     * Validates the student DTO.
     *
     * @param studentDTO the student data transfer object
     * @throws IllegalArgumentException if studentDTO is null
     */
    private void validateStudentDTO(final StudentDTO studentDTO) {
        if (null == studentDTO) {
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
     * @throws IllegalArgumentException  if studentDTO is null or ID is null
     */
    @Transactional
    public StudentDTO updateStudent(@Valid final StudentDTO studentDTO) {
        StudentService.logger.info("Updating student with ID {}", studentDTO.id());

        if (null == studentDTO.id()) {
            throw new IllegalArgumentException("Student ID cannot be null for update");
        }

        this.validateStudentDTO(studentDTO);
        final Student existingStudent = this.studentRepository.findById(studentDTO.id())
            .orElseThrow(() -> new ResourceNotFoundException(
                StudentService.STUDENT_NOT_FOUND + studentDTO.id()));

        // Update fields
        this.studentMapper.updateEntityFromDto(studentDTO, existingStudent);

        final Student savedStudent = this.studentRepository.save(existingStudent);
        StudentService.logger.info("Student updated successfully with ID {}", savedStudent.getId());

        return this.studentMapper.toDto(savedStudent);
    }
}
