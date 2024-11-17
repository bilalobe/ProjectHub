package com.projecthub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.projecthub.model.Student;
import com.projecthub.repository.custom.CustomStudentRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Service
@Tag(name = "Student Service", description = "Operations pertaining to students in ProjectHub")
public class StudentService {

    private final CustomStudentRepository studentRepository;

    public StudentService(@Qualifier("csvStudentRepository") CustomStudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Operation(summary = "View a list of all students")
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Operation(summary = "Save a student")
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    @Operation(summary = "Delete a student by ID")
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}