package com.projecthub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.projecthub.model.School;
import com.projecthub.repository.custom.CustomSchoolRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Service
@Api(value = "School Service", description = "Operations pertaining to schools in ProjectHub")
public class SchoolService {

    private final CustomSchoolRepository schoolRepository;

    public SchoolService(@Qualifier("csvSchoolRepository") CustomSchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    @ApiOperation(value = "View a list of all schools", response = List.class)
    public List<School> getAllSchools() {
        return schoolRepository.findAll();
    }

    @ApiOperation(value = "Save a school")
    public School saveSchool(School school) {
        return schoolRepository.save(school);
    }

    @ApiOperation(value = "Delete a school by ID")
    public void deleteSchool(Long id) {
        schoolRepository.deleteById(id);
    }
}