package com.projecthub.base.project.domain.template;

import com.projecthub.base.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "project_templates")
@Getter
@Setter
public class ProjectTemplate extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "project_template_phases")
    private Set<ProjectPhase> phases = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "project_template_components")
    private Set<String> requiredComponents = new HashSet<>();

    @Column(nullable = false)
    private Integer estimatedDurationDays;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private ProjectCategory category;

    public ProjectTemplate copy() {
        ProjectTemplate copy = new ProjectTemplate();
        copy.setName(this.name);
        copy.setDescription(this.description);
        copy.setPhases(new HashSet<>(this.phases));
        copy.setRequiredComponents(new HashSet<>(this.requiredComponents));
        copy.setEstimatedDurationDays(this.estimatedDurationDays);
        copy.setCategory(this.category);
        return copy;
    }
}