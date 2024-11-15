package com.projecthub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.projecthub.model.Student;
import com.projecthub.repository.custom.CustomStudentRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Service
@Api(value = "Student Service", description = "Operations pertaining to students in ProjectHub")
public class StudentService {

    private final CustomStudentRepository studentRepository;

    public StudentService(@Qualifier("csvStudentRepository") CustomStudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @ApiOperation(value = "View a list of all students", response = List.class)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @ApiOperation(value = "Save a student")
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    @ApiOperation(value = "Delete a student by ID")
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}