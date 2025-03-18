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

    public SchoolValidator() {
    }

    @Override
    public void validateCreate(@NotNull School school) {
        SchoolValidator.log.debug("Validating school creation: {}", school.getName());
        this.validateBasicFields(school);
        SchoolValidator.validateInitialState(school);
        this.validateBusinessRules(school);
        SchoolValidator.validateContact(school.getContact());
        SchoolValidator.validateAddress(school.getAddress());
    }

    @Override
    public void validateUpdate(@NotNull School school) {
        SchoolValidator.log.debug("Validating school update: {}", school.getName());
        if (school.isArchived()) {
            throw new SchoolUpdateException("Cannot update archived school");
        }
        this.validateBasicFields(school);
        this.validateCapacityRules(school);
        this.validateBusinessRules(school);
    }

    @Override
    public void validateArchive(@NotNull School school) {
        SchoolValidator.log.debug("Validating school archive: {}", school.getName());
        SchoolValidator.validateArchiveRules(school);
    }

    @Override
    public void validateDelete(@NotNull School school) {
        SchoolValidator.log.debug("Validating school deletion: {}", school.getName());
        SchoolValidator.validateDeleteRules(school);
    }

    // Basic Validations
    private void validateBasicFields(@NotNull School school) {
        if (null == school.getName() || school.getName().trim().isEmpty()) {
            throw new SchoolDomainException("School name is required");
        }
        if (100 < school.getName().length()) {
            throw new SchoolDomainException("School name cannot exceed 100 characters");
        }
        SchoolValidator.validateAddress(school.getAddress());
        SchoolValidator.validateContact(school.getContact());
        SchoolValidator.validateIdentifier(school.getIdentifier());
    }

    private static void validateAddress(@NotNull SchoolAddress address) {
        if (null == address) {
            throw new SchoolDomainException("School address is required");
        }
        // Add specific address validations
    }

    private static void validateContact(@NotNull SchoolContact contact) {
        if (null == contact) {
            throw new SchoolDomainException("School contact is required");
        }
        if (!SchoolValidator.EMAIL_PATTERN.matcher(contact.email()).matches()) {
            throw new SchoolDomainException("Invalid email format");
        }
        // Add phone number validation
    }

    private static void validateIdentifier(@NotNull SchoolIdentifier identifier) {
        if (null == identifier) {
            throw new SchoolDomainException("School identifier is required");
        }
        // Add specific identifier validations
    }

    // Capacity Validations
    private void validateCapacityRules(@NotNull School school) {
        SchoolValidator.validateCohortCapacity(school);
        SchoolValidator.validateTeamCapacity(school);
    }

    private static void validateCohortCapacity(@NotNull School school) {
        if (MAX_COHORTS <= school.getCohorts().size()) {
            throw new SchoolDomainException(
                String.format("School cannot have more than %d cohorts", Integer.valueOf(SchoolValidator.MAX_COHORTS))
            );
        }
    }

    private static void validateTeamCapacity(@NotNull School school) {
        if (MAX_TEAMS_PER_SCHOOL <= school.getTeams().size()) {
            throw new SchoolDomainException(
                String.format("School cannot have more than %d teams", Integer.valueOf(SchoolValidator.MAX_TEAMS_PER_SCHOOL))
            );
        }
    }

    // Business Rules
    private void validateBusinessRules(@NotNull School school) {
        SchoolValidator.validateUniqueCohortNames(school);
        SchoolValidator.validateTeamAssignments(school);
    }

    private static void validateUniqueCohortNames(@NotNull School school) {
        final long uniqueNames = school.getCohorts().stream()
            .map(Cohort::getName)
            .distinct()
            .count();

        if (uniqueNames != (long) school.getCohorts().size()) {
            throw new SchoolDomainException("All cohort names must be unique within a school");
        }
    }

    private static void validateTeamAssignments(@NotNull School school) {
        school.getTeams().forEach(team -> {
            if (null == team.getCohort()) {
                throw new SchoolDomainException("Each team must be assigned to a cohort");
            }
            if (!school.getCohorts().contains(team.getCohort())) {
                throw new SchoolDomainException("Team must be assigned to a cohort within the same school");
            }
        });
    }

    // Archive Validations
    private static void validateArchiveRules(@NotNull School school) {
        if (!school.getCohorts().isEmpty()) {
            throw new SchoolDomainException("Cannot archive school with active cohorts");
        }
        if (!school.getTeams().isEmpty()) {
            throw new SchoolDomainException("Cannot archive school with active teams");
        }
    }

    // Initial State Validations
    private static void validateInitialState(@NotNull School school) {
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
    private static void validateDeleteRules(@NotNull School school) {
        if (!school.getCohorts().isEmpty()) {
            throw new SchoolDomainException("Cannot delete school with active cohorts");
        }
        if (!school.getTeams().isEmpty()) {
            throw new SchoolDomainException("Cannot delete school with active teams");
        }
    }
}
