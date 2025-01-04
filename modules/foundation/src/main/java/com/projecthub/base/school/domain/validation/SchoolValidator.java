package com.projecthub.base.school.domain.validation;

import com.projecthub.base.cohort.domain.entity.Cohort;
import com.projecthub.base.school.domain.entity.School;
import com.projecthub.base.school.domain.exception.SchoolDomainException;
import com.projecthub.base.school.domain.exception.SchoolUpdateException;
import com.projecthub.base.school.domain.value.SchoolAddress;
import com.projecthub.base.school.domain.value.SchoolContact;
import com.projecthub.base.school.domain.value.SchoolIdentifier;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;


/**
 * Validator for School entities. Organized in validation groups: 1. Basic
 * validations (name, fields) 2. Capacity validations 3. Relationship
 * validations 4. Business rules 5. Archive validations
 */
@Slf4j
@Component
public class SchoolValidator implements SchoolValidation {

    private static final int MAX_COHORTS = 20;
    private static final int MAX_TEAMS_PER_SCHOOL = 50;
    private static final int MAX_NAME_LENGTH = 100;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Override
    public void validateCreate(@NotNull final School school) {
        log.debug("Validating school creation: {}", school.getName());
        validateBasicFields(school);
        validateInitialState(school);
        validateBusinessRules(school);
        validateContact(school.getContact());
        validateAddress(school.getAddress());
    }

    @Override
    public void validateUpdate(@NotNull final School school) {
        log.debug("Validating school update: {}", school.getName());
        if (school.isArchived()) {
            throw new SchoolUpdateException("Cannot update archived school");
        }
        validateBasicFields(school);
        validateCapacityRules(school);
        validateBusinessRules(school);
    }

    @Override
    public void validateArchive(@NotNull final School school) {
        log.debug("Validating school archive: {}", school.getName());
        validateArchiveRules(school);
    }

    @Override
    public void validateDelete(@NotNull final School school) {
        log.debug("Validating school deletion: {}", school.getName());
        validateDeleteRules(school);
    }

    // Basic Validations
    private void validateBasicFields(@NotNull final School school) {
        if (school.getName() == null || school.getName().trim().isEmpty()) {
            throw new SchoolDomainException("School name is required");
        }
        if (school.getName().length() > 100) {
            throw new SchoolDomainException("School name cannot exceed 100 characters");
        }
        validateAddress(school.getAddress());
        validateContact(school.getContact());
        validateIdentifier(school.getIdentifier());
    }

    private void validateAddress(@NotNull final SchoolAddress address) {
        if (address == null) {
            throw new SchoolDomainException("School address is required");
        }
        // Add specific address validations
    }

    private void validateContact(@NotNull final SchoolContact contact) {
        if (contact == null) {
            throw new SchoolDomainException("School contact is required");
        }
        if (!EMAIL_PATTERN.matcher(contact.email()).matches()) {
            throw new SchoolDomainException("Invalid email format");
        }
        // Add phone number validation
    }

    private void validateIdentifier(@NotNull final SchoolIdentifier identifier) {
        if (identifier == null) {
            throw new SchoolDomainException("School identifier is required");
        }
        // Add specific identifier validations
    }

    // Capacity Validations
    private void validateCapacityRules(@NotNull final School school) {
        validateCohortCapacity(school);
        validateTeamCapacity(school);
    }

    private void validateCohortCapacity(@NotNull final School school) {
        if (school.getCohorts().size() >= MAX_COHORTS) {
            throw new SchoolDomainException(
                String.format("School cannot have more than %d cohorts", MAX_COHORTS)
            );
        }
    }

    private void validateTeamCapacity(@NotNull final School school) {
        if (school.getTeams().size() >= MAX_TEAMS_PER_SCHOOL) {
            throw new SchoolDomainException(
                String.format("School cannot have more than %d teams", MAX_TEAMS_PER_SCHOOL)
            );
        }
    }

    // Business Rules
    private void validateBusinessRules(@NotNull final School school) {
        validateUniqueCohortNames(school);
        validateTeamAssignments(school);
    }

    private void validateUniqueCohortNames(@NotNull final School school) {
        long uniqueNames = school.getCohorts().stream()
            .map(Cohort::getName)
            .distinct()
            .count();

        if (uniqueNames != school.getCohorts().size()) {
            throw new SchoolDomainException("All cohort names must be unique within a school");
        }
    }

    private void validateTeamAssignments(@NotNull final School school) {
        school.getTeams().forEach(team -> {
            if (team.getCohort() == null) {
                throw new SchoolDomainException("Each team must be assigned to a cohort");
            }
            if (!school.getCohorts().contains(team.getCohort())) {
                throw new SchoolDomainException("Team must be assigned to a cohort within the same school");
            }
        });
    }

    // Archive Validations
    private void validateArchiveRules(@NotNull final School school) {
        if (!school.getCohorts().isEmpty()) {
            throw new SchoolDomainException("Cannot archive school with active cohorts");
        }
        if (!school.getTeams().isEmpty()) {
            throw new SchoolDomainException("Cannot archive school with active teams");
        }
    }

    // Initial State Validations
    private void validateInitialState(@NotNull final School school) {
        if (!school.isActive()) {
            throw new SchoolDomainException("New schools must be active");
        }
        if (!school.getCohorts().isEmpty()) {
            throw new SchoolDomainException("New schools cannot have pre-existing cohorts");
        }
        if (!school.getTeams().isEmpty()) {
            throw new SchoolDomainException("New schools cannot have pre-existing teams");
        }
    }

    // Delete Validations
    private void validateDeleteRules(@NotNull final School school) {
        if (!school.getCohorts().isEmpty()) {
            throw new SchoolDomainException("Cannot delete school with active cohorts");
        }
        if (!school.getTeams().isEmpty()) {
            throw new SchoolDomainException("Cannot delete school with active teams");
        }
    }
}
