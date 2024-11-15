package com.projecthub.model;

import jakarta.persistence.*;

@Entity
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String timestamp;
    private Integer grade;

    // Many-to-One relationship with Student
    @Column(name = "student_id")
    private Long studentId;

    // Many-to-One relationship with Project
    @Column(name = "project_id")
    private Long projectId;

    // Default constructor required by JPA
    public Submission() {
    }

    // Constructor with all fields
    public Submission(Long id, Long projectId, Long studentId, String filePath, Integer grade) {
        this.id = id;
        this.projectId = projectId;
        this.studentId = studentId;
        this.content = filePath;
        this.grade = grade;
    }

    // Constructor without id (for new Submissions)
    public Submission(Long studentId, Long projectId, String content, String timestamp) {
        this.studentId = studentId;
        this.projectId = projectId;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getSubmissionId() {
        return id;
    }

    public Integer getGrade() {
        return grade;
    }

    public String getFilePath() {
        return "/submissions/" + id + "/" + content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setSubmissionId(Long id) {
        this.id = id;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    // Override toString method
    @Override
    public String toString() {
        return "Submission{id=" + id + ", content='" + content + "', timestamp='" + timestamp 
               + "', studentId=" + studentId + ", projectId=" + projectId 
               + ", grade=" + grade + "}";
    }
}