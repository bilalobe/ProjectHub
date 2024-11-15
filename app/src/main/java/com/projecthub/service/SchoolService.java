package com.projecthub.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.projecthub.model.School;
import com.projecthub.repository.custom.CustomSchoolRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Service
@Tag(name = "School Service", description = "Operations pertaining to schools in ProjectHub")
public class SchoolService {

    private final CustomSchoolRepository schoolRepository;

    @Autowired
    public SchoolService(@Qualifier("csvSchoolRepository") CustomSchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    @Operation(summary = "View a list of all schools", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved list of schools")
    })
    public List<School> getAllSchools() {
        return schoolRepository.findAll();
    }

    @Operation(summary = "Save a school", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully saved school"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid school data")
    })
    public School saveSchool(School school) {
        return schoolRepository.save(school);
    }

    @Operation(summary = "Delete a school by ID", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully deleted school"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found")
    })
    public void deleteSchool(Long id) {
        schoolRepository.deleteById(id);
    }

    public Optional<School> getSchoolById(Long id) {
        return schoolRepository.findById(id);
    }
}