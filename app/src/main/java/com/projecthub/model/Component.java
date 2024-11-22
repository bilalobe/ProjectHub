package com.projecthub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Represents a component in a project.
 */
@Entity
public class Component {

    /**
     * The unique identifier for the component.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the component.
     */
    private String name;

    /**
     * The description of the component.
     */
    private String description;

    /**
     * The project to which this component belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    public Component() {}

    public Component(Long id, String name, String description, Project project) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.project = project;
    }

    public Component(String name, String description, Project project) {
        this.name = name;
        this.description = description;
        this.project = project;
    }

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

    @Override
    public String toString() {
        return "Component{id=" + id + ", name='" + name + "', description='" + description + "', project=" + project + "}";
    }
}