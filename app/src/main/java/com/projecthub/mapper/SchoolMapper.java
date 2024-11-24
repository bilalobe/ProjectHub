package com.projecthub.mapper;

import org.springframework.stereotype.Component;

import com.projecthub.dto.SchoolSummary;
import com.projecthub.model.School;

@Component
public class SchoolMapper {

    public School toSchool(SchoolSummary schoolSummary) {
        if (schoolSummary == null) {
            return null;
        }
        School school = new School();
        school.setId(schoolSummary.getId());
        school.setName(schoolSummary.getName());
        school.setAddress(schoolSummary.getAddress());
        return school;
    }

    public SchoolSummary toSchoolSummary(School school) {
        if (school == null) {
            return null;
        }
        return new SchoolSummary(
            school.getId(),
            school.getName(),
            school.getAddress()
        );
    }
}