package com.projecthub.base.student.application.service;

import com.projecthub.base.shared.domain.enums.status.ActivationStatus;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import com.projecthub.base.student.api.dto.StudentDTO;
import com.projecthub.base.student.api.mapper.StudentMapper;
import com.projecthub.base.student.domain.entity.Student;
import com.projecthub.base.student.domain.repository.StudentJpaRepository;
import com.projecthub.base.student.infrastructure.persistence.specification.StudentSearchCriteria;
import com.projecthub.base.student.infrastructure.persistence.specification.StudentSpecification;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class StudentQueryService {
    private static final Logger logger = LoggerFactory.getLogger(StudentQueryService.class);
    @NonNls
    @NonNls
    private static final String STUDENT_NOT_FOUND = "Student not found with ID: ";

    private final StudentJpaRepository repository;
    private final StudentMapper mapper;

    public StudentQueryService(final StudentJpaRepository repository, final StudentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Page<StudentDTO> searchStudents(com.projecthub.base.student.api.dto.StudentSearchCriteria criteria, Pageable pageable) {
        logger.debug("Searching students with criteria: {}", criteria);
        return repository.findAll(
            StudentSpecification.withDynamicQuery(criteria),
            pageable
        ).map(mapper::toDto);
    }

    public List<StudentDTO> searchStudents(StudentSearchCriteria criteria) {
        logger.debug("Searching students with criteria (non-paginated): {}", criteria);
        return repository.findAll(
            StudentSpecification.withDynamicQuery(criteria)
        ).stream()
            .map(mapper::toDto)
            .toList();
    }

    public StudentDTO getById(final UUID id) {
        return this.repository.findById(id)
            .map(this.mapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException(STUDENT_NOT_FOUND + id));
    }

    public StudentDTO getByStudentId(final UUID studentId) {
        return this.repository.findByStudentId(studentId)
            .map(this.mapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with student ID: " + studentId));
    }

    public List<StudentDTO> getByTeamId(final UUID teamId) {
        logger.debug("Retrieving students for team ID: {}", teamId);
        return this.repository.findAll(StudentSpecification.byTeamId(teamId)).stream()
            .map(this.mapper::toDto)
            .toList();
    }

    public List<StudentDTO> getByStatus(final ActivationStatus status) {
        logger.debug("Retrieving students with status: {}", status);
        return this.repository.findAll(StudentSpecification.byStatus(status)).stream()
            .map(this.mapper::toDto)
            .toList();
    }

    public List<StudentDTO> getByTeamIdAndStatus(final UUID teamId, final ActivationStatus status) {
        logger.debug("Retrieving students for team ID: {} with status: {}", teamId, status);
        Specification<Student> spec = Specification.where(StudentSpecification.byTeamId(teamId))
            .and(StudentSpecification.byStatus(status));

        return this.repository.findAll(spec).stream()
            .map(this.mapper::toDto)
            .toList();
    }

    public boolean existsByEmail(final String email) {
        return this.repository.existsByEmail(email);
    }

    public StudentDTO getByEmail(@NonNls final String email) {
        return this.repository.findByEmail(email)
            .map(this.mapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + email));
    }

    public List<StudentDTO> getAllStudents() {
        logger.debug("Retrieving all students");
        return this.repository.findAll().stream()
            .map(this.mapper::toDto)
            .toList();
    }

    public Student findById(UUID id) {
        return this.repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(STUDENT_NOT_FOUND + id));
    }
}
