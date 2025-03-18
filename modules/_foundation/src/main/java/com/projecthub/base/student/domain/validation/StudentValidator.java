package com.projecthub.base.student.domain.validation;

import com.projecthub.base.shared.exception.ValidationException;
import com.projecthub.base.student.domain.entity.Student;
import com.projecthub.base.student.domain.repository.StudentJpaRepository;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StudentValidator {
    private final StudentJpaRepository repository;
    private final Validator validator;

    public void validateCreate(Student student) {
        var violations = validator.validate(student);
        if (!violations.isEmpty()) {
            throw new ValidationException("Invalid student data", violations);
        }

        if (repository.existsByEmail(student.getEmail())) {
            throw new ValidationException("Email already exists: " + student.getEmail());
        }

        if (repository.existsByStudentId(student.getStudentId())) {
            throw new ValidationException("Student ID already exists: " + student.getStudentId());
        }
    }

    public void validateUpdate(Student student, UUID id) {
        var violations = validator.validate(student);
        if (!violations.isEmpty()) {
            throw new ValidationException("Invalid student data", violations);
        }

        repository.findByEmail(student.getEmail())
            .ifPresent(existing -> {
                if (!Student.getId().equals(id)) {
                    throw new ValidationException("Email already exists: " + student.getEmail());
                }
            });

        repository.findByStudentId(student.getStudentId())
            .ifPresent(existing -> {
                if (!Student.getId().equals(id)) {
                    throw new ValidationException("Student ID already exists: " + student.getStudentId());
                }
            });
    }
}
