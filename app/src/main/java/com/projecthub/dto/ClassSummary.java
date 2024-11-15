package com.projecthub.dto;

public class ClassSummary {
    private Long id;
    private String name;
    private Long schoolId;

    public ClassSummary() {}

    public ClassSummary(Long id, String name, Long schoolId) {
        this.id = id;
        this.name = name;
        this.schoolId = schoolId;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }
}