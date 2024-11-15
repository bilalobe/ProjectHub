package com.projecthub.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projecthub.model.Class;
import com.projecthub.repository.jpa.ClassRepository;

@Service
public class ClassService {

    private final ClassRepository classRepository;

    @Autowired
    public ClassService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    public List<Class> getAllClasses() {
        return classRepository.findAll();
    }

    public List<Class> getClassesBySchoolId(Long schoolId) {
        return classRepository.findBySchoolId(schoolId);
    }

    public Optional<Class> getClassById(Long id) {
        return classRepository.findById(id);
    }

    public Class saveClass(Class classEntity) {
        return classRepository.save(classEntity);
    }

    public void deleteClass(Long id) {
        classRepository.deleteById(id);
    }
}