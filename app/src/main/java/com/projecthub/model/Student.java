package com.projecthub.model;

import jakarta.persistence.*;
import java.util.List;

/**
 * Represents a student in the system.
 * A student can have multiple submissions.
 */
@Entity
public class Student {

    /**
     * The unique identifier for the student.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The student's name.
     */
    private String name;

    /**
     * The list of submissions made by the student.
     */
    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Submission> submissions;

    /**
     * Default constructor required by JPA.
     */
    public Student() {
    }

    /**
     * Constructs a new student with the specified ID, name, and submissions.
     *
     * @param id          the student's ID
     * @param name        the student's name
     * @param submissions the list of submissions
     */
    public Student(Long id, String name, List<Submission> submissions) {
        this.id = id;
        this.name = name;
        this.submissions = submissions;
    }

    /**
     * Constructs a new student with the specified name.
     *
     * @param name the student's name
     */
    public Student(String name) {
        this.name = name;
    }

    // Getters and Setters

    /**
     * Returns the student's ID.
     *
     * @return the student's ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the student's ID.
     *
     * @param id the student's ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the student's name.
     *
     * @return the student's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the student's name.
     *
     * @param name the student's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the list of submissions made by the student.
     *
     * @return the list of submissions
     */
    public List<Submission> getSubmissions() {
        return submissions;
    }

    /**
     * Sets the list of submissions made by the student.
     *
     * @param submissions the list of submissions
     */
    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    /**
     * Returns a string representation of the student.
     *
     * @return a string containing the student's ID and name
     */
    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "'}";
    }
}