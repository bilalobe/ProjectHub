package com.projecthub.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // One-to-Many relationship with Submission
    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Submission> submissions;

    // Default constructor required by JPA
    public Student() {
    }

    // Constructor with all fields
    public Student(Long id, String name, List<Submission> submissions) {
        this.id = id;
        this.name = name;
        this.submissions = submissions;
    }

    // Constructor without id (for new Students)
    public Student(String name) {
        this.name = name;
    }

    public Student(long parseLong, String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    // Override toString method
    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "'}";
    }
}