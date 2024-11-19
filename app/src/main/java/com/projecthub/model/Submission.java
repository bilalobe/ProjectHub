package com.projecthub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Represents a submission made by a student for a project.
 */
@Entity
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String timestamp;
    private Integer grade;
    private String filePath;

    /**
     * The student who made the submission.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    /**
     * The project for which the submission was made.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    // Default constructor required by JPA
    public Submission(Long long1, Student student2, Project project2, String nextLine, Integer integer) {
    }

    // Constructor with all fields (excluding redundant IDs)
    public Submission(Long id, Student student, Project project, String content, String timestamp, Integer grade) {
        this.id = id;
        this.student = student;
        this.project = project;
        this.content = content;
        this.timestamp = timestamp;
        this.grade = grade;
    }

    // Constructor without id (for new Submissions)
    public Submission(Student student, Project project, String content, String timestamp) {
        this.student = student;
        this.project = project;
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

    public Integer getGrade() {
        return grade;
    }

    public Student getStudent() {
        return student;
    }

    public Project getProject() {
        return project;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    // Override toString method
    @Override
    public String toString() {
        return "Submission{id=" + id + 
               ", content='" + content + '\'' +
               ", timestamp='" + timestamp + '\'' +
               ", studentId=" + (student != null ? student.getId() : null) +
               ", projectId=" + (project != null ? project.getId() : null) +
               ", grade=" + grade + 
               '}';
    }
}