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

    private Long studentId;

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

    // Constructor with student and project entities
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

    public Long getStudentId() {
        return student != null ? student.getId() : null;
    }

    public Long getProjectId() {
        return project != null ? project.getId() : null;
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

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    // Override toString method
    @Override
    public String toString() {
        return "Submission{id=" + id + ", content='" + content + "', timestamp='" + timestamp
                + "', studentId=" + studentId + ", projectId=" + projectId
                + ", grade=" + grade + "}";
    }
}
