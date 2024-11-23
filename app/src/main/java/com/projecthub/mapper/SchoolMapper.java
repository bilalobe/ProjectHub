package com.projecthub.mapper;

import com.projecthub.dto.SchoolSummary;
import com.projecthub.model.School;

public class SchoolMapper {

    public static School toSchool(SchoolSummary schoolSummary) {
        School school = new School();
        school.setId(schoolSummary.getId());
        school.setName(schoolSummary.getName());
        school.setAddress(schoolSummary.getAddress());
        return school;
    }

    public static SchoolSummary toSchoolSummary(School school) {
        return new SchoolSummary(
            school.getId(),
            school.getName(),
            school.getAddress()
        );
    }
}