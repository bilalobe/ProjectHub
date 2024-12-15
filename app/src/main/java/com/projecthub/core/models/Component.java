package com.projecthub.core.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@Getter
@Setter
public class Component extends BaseEntity {

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
    @JoinColumn(nullable = false)
    private Project project;

    /**
     * Default constructor required by JPA.
     */
    public Component() {
    }

    public Component(String name, String description, Project project) {
        this.name = name;
        this.description = description;
        this.project = project;
    }
}