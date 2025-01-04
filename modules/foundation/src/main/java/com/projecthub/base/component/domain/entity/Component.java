package com.projecthub.base.component.domain.entity;

import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;

/**
 * Represents a component within a project in the ProjectHub system.
 * <p>
 * Components are discrete parts or modules of a project that can be developed
 * and tracked independently. Each component belongs to exactly one project and
 * has its own description and requirements.
 * </p>
 * <p>
 * Business rules:
 * <ul>
 *   <li>Components must belong to exactly one project</li>
 *   <li>Component names must be unique within a project</li>
 *   <li>Components require both a name and description</li>
 *   <li>Components are deleted when their parent project is deleted</li>
 * </ul>
 * </p>
 *
 * @see Project
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "project")
public class Component extends BaseEntity {

    /**
     * The name of the component.
     * <p>
     * Identifies the component within its project. Should be descriptive
     * and indicate the component's purpose or functionality.
     * </p>
     */
    @NotBlank(message = "Component name is mandatory")
    @Size(max = 100, message = "Component name must be less than 100 characters")
    @Column(nullable = false)
    private String name;

    /**
     * The detailed description of the component.
     * <p>
     * Provides information about the component's purpose, requirements,
     * and implementation details. Limited to 500 characters.
     * </p>
     */
    @NotBlank(message = "Description is mandatory")
    @Size(max = 500, message = "Description must be less than 500 characters")
    @Column(nullable = false)
    private String description;

    @NotBlank
    @Pattern(regexp = "^\\d+\\.\\d+\\.\\d+$")
    private String version = "0.1.0";


    @Size(max = 500)
    private String technicalDetails;

    private String documentationUrl;

    /**
     * The project to which this component belongs.
     */
    @NotNull(message = "Project is mandatory")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Project project;


    public Component(String name, String description, Project project) {
        this.name = name;
        this.description = description;
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Component component = (Component) o;
        return Objects.equals(name, component.name) &&
            Objects.equals(project, component.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, project);
    }
}
