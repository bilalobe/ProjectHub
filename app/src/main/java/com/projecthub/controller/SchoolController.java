package com.projecthub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projecthub.dto.SchoolSummary;
import com.projecthub.service.SchoolService;

@RestController
@RequestMapping("/schools")
public class SchoolController {

    private final SchoolService schoolService;

    @Autowired
    public SchoolController(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    @GetMapping
    public List<SchoolSummary> getAllSchools() {
        return schoolService.getAllSchools();
    }

    @GetMapping("/{id}")
    public SchoolSummary getSchoolById(@PathVariable Long id) {
        return schoolService.getSchoolById(id)
                .orElseThrow(() -> new RuntimeException("School not found"));
    }

    @PostMapping
    public SchoolSummary createSchool(@RequestBody SchoolSummary schoolSummary) {
        return schoolService.saveSchool(schoolSummary);
    }

    @PutMapping("/{id}")
    public SchoolSummary updateSchool(@PathVariable Long id, @RequestBody SchoolSummary schoolSummary) {
        schoolSummary.setId(id);
        return schoolService.saveSchool(schoolSummary);
    }

    @DeleteMapping("/{id}")
    public void deleteSchool(@PathVariable Long id) {
        schoolService.deleteSchool(id);
    }
}