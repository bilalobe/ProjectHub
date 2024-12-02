package com.projecthub.repository;

import com.projecthub.model.Student;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Student} entities.
 */
public interface StudentRepository {
    Student save(Student student);
    List<Student> findAll();
    Optional<Student> findById(Long id);
    void deleteById(Long id);
    Optional<Student> findByUsername(String username);
    List<Student> findByTeamId(Long teamId);
}