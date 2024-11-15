package com.projecthub.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projecthub.model.Class;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    List<Class> findBySchoolId(Long schoolId);
}