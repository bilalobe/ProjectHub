package com.projecthub.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents a component in a project.
 * <p>
 * Components are building blocks of a project and linked to a {@link Project}.
 * </p>
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    indexes = {
        @Index(name = "idx_component_name", columnList = "name")
    }
)
public class Component {

    /**
     * The unique identifier for the component.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * The name of the component.
     */
    @NotBlank(message = "Component name is mandatory")
    @Size(max = 100, message = "Component name must be less than 100 characters")
    @Column(nullable = false)
    private String name;

    /**
     * The description of the component.
     */
    @NotBlank(message = "Description is mandatory")
    @Size(max = 500, message = "Description must be less than 500 characters")
    @Column(nullable = false)
    private String description;

    /**
     * The project to which this component belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    private String createdBy;

    private boolean deleted = false;

    public Component() {}

    public Component(UUID id, String name, String description, Project project) {
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

    public UUID getId() {
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

    public void setId(UUID id) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Component{id=" + id + ", name='" + name + "', description='" + description + "', project=" + project + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", createdBy='" + createdBy + "', deleted=" + deleted + "}";
    }
}