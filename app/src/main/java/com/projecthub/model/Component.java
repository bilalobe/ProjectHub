package com.projecthub.model;

import jakarta.persistence.*;

/**
 * Represents a component within a project.
 * Components are modular pieces that make up a project.
 */
@Entity
public class Component {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    // Many-to-One relationship with Project
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    // Default constructor required by JPA
    public Component() {
    }

    // Constructor with all fields
    public Component(Long id, String name, String description, Project project) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.project = project;
    }

    // Constructor without id (for new Components)
    public Component(String name, String description, Project project) {
        this.name = name;
        this.description = description;
        this.project = project;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Project getProject() {
        return project;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    // Override toString method
    @Override
    public String toString() {
        return "Component{id=" + id + ", name='" + name + "', description='" + description + "', project=" + project + "}";
    }
}