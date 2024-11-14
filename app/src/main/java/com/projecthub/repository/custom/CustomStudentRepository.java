package com.projecthub.repository.custom;

import java.util.List;

import com.projecthub.model.Student;

public interface CustomStudentRepository {
    List<Student> findAll();
    Student save(Student student);
    void deleteById(Long studentId);
}