package com.projecthub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.projecthub.model.School;
import com.projecthub.repository.custom.CustomSchoolRepository;

@Service
public class SchoolService {

    private final CustomSchoolRepository schoolRepository;

    public SchoolService(@Qualifier("csvSchoolRepository") CustomSchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    public List<School> getAllSchools() {
        return schoolRepository.findAll();
    }

    public School saveSchool(School school) {
        return schoolRepository.save(school);
    }

    public void deleteSchool(Long id) {
        schoolRepository.deleteById(id);
    }
}