package com.projecthub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents a submission made by a student for a project.
 * This entity is mapped to the "Submission" table in the database.
 */
@Entity
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Content is mandatory")
    @Size(max = 5000, message = "Content must be less than 5000 characters")
    private String content;

    @NotBlank(message = "Timestamp is mandatory")
    private String timestamp;

    private Integer grade;

    @Size(max = 255, message = "File path must be less than 255 characters")
    private String filePath;

    /**
     * The student who made the submission.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @NotNull(message = "Student is mandatory")
    private Student student;

    /**
     * The project for which the submission was made.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @NotNull(message = "Project is mandatory")
    private Project project;

    // Default constructor required by JPA
    public Submission() {
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

    @Override
    public String toString() {
        return "Submission{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", grade=" + grade +
                ", filePath='" + filePath + '\'' +
                ", studentId=" + (student != null ? student.getId() : null) +
                ", projectId=" + (project != null ? project.getId() : null) +
                '}';
    }
}