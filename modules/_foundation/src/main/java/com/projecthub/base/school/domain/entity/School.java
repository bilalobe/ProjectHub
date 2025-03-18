package com.projecthub.base.school.domain.entity;

import com.projecthub.base.cohort.domain.entity.Cohort;
import com.projecthub.base.school.application.port.in.command.UpdateSchoolCommand;
import com.projecthub.base.school.domain.exception.SchoolUpdateException;
import com.projecthub.base.school.domain.value.SchoolAddress;
import com.projecthub.base.school.domain.value.SchoolContact;
import com.projecthub.base.school.domain.value.SchoolIdentifier;
import com.projecthub.base.shared.domain.entity.BaseEntity;
import com.projecthub.base.team.domain.entity.Team;
import jakarta.persistence.*;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.jetbrains.annotations.NonNls;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "schools")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class School extends BaseEntity {

    private static final int MAX_COHORTS_PER_SCHOOL = 10;

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Cohort> cohorts = new ArrayList<>();

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Team> teams = new ArrayList<>();

    @NonNls
    @NotBlank
    @Size(max = 100)
    @Column(unique = true, nullable = false)
    private String name;

    @NotNull
    @Embedded
    private SchoolAddress address;

    @NotNull
    @Embedded
    private SchoolContact contact;

    @NotNull
    @Embedded
    private SchoolIdentifier identifier;

    @Version
    private Long version;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean archived;

    // Domain methods
    public void update(final UpdateSchoolCommand command) {
        this.validateStateForUpdate();
        Objects.requireNonNull(command, "Update command cannot be null");

        if (!command.name().equals(name)) {
            name = command.name();
        }

        if (!command.address().equals(address)) {
            address = SchoolAddress.from(command.address());
        }

        if (!command.contact().equals(contact)) {
            contact = SchoolContact.from(command.contact());
        }
    }

    private void validateStateForUpdate() {
        if (archived) {
            throw new SchoolUpdateException("Cannot update archived school");
        }
    }

    public void archive() {
        if (archived) {
            throw new IllegalStateException("School is already archived");
        }
        active = false;
        archived = true;
    }

    public void addCohort(final Cohort cohort) {
        if (MAX_COHORTS_PER_SCHOOL <= cohorts.size()) {
            throw new ValidationException("Maximum cohorts reached");
        }
        this.cohorts.add(cohort);
    }

    public boolean hasActiveCohorts() {
        return this.cohorts.stream()
            .anyMatch(c -> !c.isCompleted());
    }
}
