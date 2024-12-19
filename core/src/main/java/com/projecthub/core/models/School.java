package com.projecthub.core.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

/**
 * Represents a school in the system.
 * <p>
 * A school contains multiple cohorts and teams.
 * </p>
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        indexes = {
                @Index(name = "idx_school_name", columnList = "name")
        }
)
@Getter
@Setter
public class School extends BaseEntity {

    /**
     * The name of the school.
     */
    @NotBlank(message = "School name is mandatory")
    @Size(max = 100, message = "School name must be less than 100 characters")
    @Column(unique = true, nullable = false)
    private String name;

    /**
     * The address of the school.
     */
    @NotBlank(message = "Address is mandatory")
    @Size(max = 200, message = "Address must be less than 200 characters")
    @Column(nullable = false)
    private String address;

    /**
     * The list of teams in the school.
     */
    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams;

    /**
     * The list of cohorts in the school.
     */
    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cohort> cohorts;

    // Default constructor required by JPA
    public School() {
    }

    /**
     * Constructor with all fields.
     */
    public School(String name, String address, List<Team> teams, List<Cohort> cohorts) {
        this.name = name;
        this.address = address;
        this.teams = teams;
        this.cohorts = cohorts;
    }

    /**
     * Constructor without id (for new Schools).
     */
    public School(String name, String address) {
        this.name = name;
        this.address = address;
    }
}