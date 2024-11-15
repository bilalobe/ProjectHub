package com.projecthub.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projecthub.model.Student;
import com.projecthub.repository.custom.CustomStudentRepository;

@Repository("postgresStudentRepository")
public interface StudentRepository extends JpaRepository<Student, Long>, CustomStudentRepository {
    // Custom query methods can be added here
}