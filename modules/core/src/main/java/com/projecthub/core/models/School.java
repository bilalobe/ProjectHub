package com.projecthub.core.models;

import com.projecthub.core.entities.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;
import java.util.Objects;

/**
 * Represents an educational institution in the ProjectHub system.
 * <p>
 * Schools are the top-level organizational units that contain cohorts and teams.
 * Each school represents a distinct educational institution with its own address
 * and administrative structure.
 * </p>
 * <p>
 * Business rules:
 * <ul>
 *   <li>Each school must have a unique name</li>
 *   <li>Schools can have multiple cohorts</li>
 *   <li>Schools can have multiple teams</li>
 *   <li>Deleting a school cascades to all its cohorts and teams</li>
 *   <li>School names and addresses must not exceed specified lengths</li>
 * </ul>
 * </p>
 *
 * @see Cohort
 * @see Team
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
@ToString(exclude = {"teams", "cohorts"})
public class School extends BaseEntity {

    /**
     * The name of the school.
     * <p>
     * Serves as the business identifier for the school. Must be unique
     * across all schools in the system.
     * </p>
     */
    @NotBlank(message = "School name is mandatory")
    @Size(max = 100, message = "School name must be less than 100 characters")
    @Column(unique = true, nullable = false)
    private String name;

    /**
     * The physical address of the school.
     * <p>
     * Required for administrative purposes and location identification.
     * Must be a valid postal address.
     * </p>
     */
    @NotBlank(message = "Address is mandatory")
    @Size(max = 200, message = "Address must be less than 200 characters")
    @Column(nullable = false)
    private String address;

    @NotBlank(message = "Contact email is mandatory")
    @Email(message = "Contact email must be valid")
    @Column(nullable = false)
    private String contactEmail;

    @Size(max = 50)
    private String accreditationNumber;

    @Size(max = 20)
    private String phoneNumber;

    private String websiteUrl;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        School school = (School) o;
        return Objects.equals(name, school.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}