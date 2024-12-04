package com.projecthub.repository;

import com.projecthub.model.Student;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link Student} entities.
 */
public interface StudentRepository {
    Student save(Student student);
    List<Student> findAll();
    Optional<Student> findById(UUID id);
    void deleteById(UUID id);
    Optional<Student> findByUsername(String username);
    List<Student> findByTeamId(UUID teamId);
    List<Student> findByCohortId(UUID cohortId);
}