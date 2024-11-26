package com.projecthub.repository.custom;

import java.util.List;
import java.util.Optional;

import com.projecthub.model.Student;

public interface CustomStudentRepository {
    List<Student> findAll();
    Student save(Student student);
    void deleteById(Long studentId);

    public Optional<Student> findById(Long id);
    void delete(Student student);
}