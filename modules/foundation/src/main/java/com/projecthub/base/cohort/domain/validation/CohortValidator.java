package com.projecthub.base.cohort.domain.validation;

import com.projecthub.base.cohort.domain.entity.Cohort;
import com.projecthub.base.cohort.domain.enums.GradeLevel;
import com.projecthub.base.cohort.domain.value.CohortAssignment;
import com.projecthub.base.school.domain.entity.School;
import com.projecthub.base.shared.exception.ValidationException;
import com.projecthub.base.team.domain.entity.Team;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

/**
 * Validator for Cohort entities. Validates business rules related to:
 * - Student capacity limits
 * - Cohorts per year limits
 * - Grade level assignments
 * - Year scheduling
 */
@Slf4j
@Component
public class CohortValidator {

    private static final int MAX_STUDENTS = 50;
    private static final int MAX_COHORTS_PER_YEAR = 4;
    private static final int MIN_STUDENTS = 10;
    private static final String ERROR_PREFIX = "COHORT_";

    public void validateCreate(Cohort cohort, School school) {
        log.debug("Validating cohort creation for school: {}", school.getId());
        validateBasicFields(cohort, ValidationError.INVALID_NAME);
        validateDates(cohort, ValidationError.INVALID_DATES);
        validateCapacity(cohort, ValidationError.INVALID_CAPACITY);
        validateSchoolAssignment(cohort, school);
        validateUniqueName(cohort, school);
    }

    public void validateUpdate(Cohort cohort, School school) {
        log.debug("Validating cohort update for school: {}", school.getId());
        if (cohort.isArchived()) {
            throw new ValidationException(ValidationError.ARCHIVED_MODIFICATION.code,
                "Cannot update archived cohort");
        }
        validateBasicFields(cohort, ValidationError.INVALID_NAME);
        validateDates(cohort, ValidationError.INVALID_DATES);
        validateCapacity(cohort, ValidationError.INVALID_CAPACITY);
        validateStateTransition(cohort);
    }

    public void validateAddTeam(Cohort cohort, Team team) {
        log.debug("Validating team addition to cohort: {}", cohort.getId());
        if (team == null) {
            throw new ValidationException("Team cannot be null");
        }

        if (cohort.isArchived()) {
            throw new ValidationException("Cannot add team to archived cohort");
        }

        if (cohort.getTeams().size() >= cohort.getAssignment().maxTeams()) {
            throw new ValidationException("Maximum number of teams reached");
        }
    }

    public void validateRemoveTeam(Cohort cohort, Team team) {
        log.debug("Validating team removal from cohort: {}", cohort.getId());
        if (team == null) {
            throw new ValidationException("Team cannot be null");
        }

        if (!cohort.getTeams().contains(team)) {
            throw new ValidationException("Team is not part of this cohort");
        }
    }

    public void validateMarkAsCompleted(Cohort cohort) {
        log.debug("Validating cohort completion: {}", cohort.getId());
        if (cohort.isArchived()) {
            throw new ValidationException("Cannot complete an archived cohort");
        }

        if (LocalDate.now().isBefore(cohort.getEndTerm())) {
            throw new ValidationException("Cannot complete cohort before end term");
        }
    }

    public void validateArchive(Cohort cohort, String reason) {
        log.debug("Validating cohort archival: {}", cohort.getId());
        if (cohort.isArchived()) {
            throw new ValidationException("Cohort is already archived");
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException("Archive reason is required");
        }
    }

    public void validateUpdateDetails(Cohort cohort, LocalDate startTerm, LocalDate endTerm) {
        log.debug("Validating cohort detail update: {}", cohort.getId());
        if (startTerm == null || endTerm == null) {
            throw new ValidationException("Start and end terms are required");
        }

        if (startTerm.isAfter(endTerm)) {
            throw new ValidationException("Start term cannot be after end term");
        }

        if (cohort.isArchived()) {
            throw new ValidationException("Cannot update archived cohort");
        }
    }

    public void validateUpdateMaxStudents(Cohort cohort, int maxStudents) {
        log.debug("Validating max students update: {}", cohort.getId());
        if (cohort.isArchived()) {
            throw new ValidationException("Cannot update archived cohort");
        }

        if (maxStudents < MIN_STUDENTS || maxStudents > MAX_STUDENTS) {
            throw new ValidationException(
                String.format("Max students must be between %d and %d",
                    MIN_STUDENTS, MAX_STUDENTS));
        }
    }

    private void validateBasicFields(Cohort cohort, ValidationError error) {
        if (cohort == null || !StringUtils.hasText(cohort.getName())) {
            throw new ValidationException(error.code, "Cohort name is required");
        }
        if (cohort.getName().length() > 100) {
            throw new ValidationException(error.code, "Cohort name must be less than 100 characters");
        }
    }

    private void validateDates(Cohort cohort, ValidationError error) {
        if (cohort.getStartTerm() == null || cohort.getEndTerm() == null) {
            throw new ValidationException(error.code, "Start and end terms are required");
        }
        if (cohort.getStartTerm().isAfter(cohort.getEndTerm())) {
            throw new ValidationException(error.code, "Start term must be before end term");
        }
        if (cohort.getStartTerm().isBefore(LocalDate.now().minusYears(1))) {
            throw new ValidationException(error.code, "Start term cannot be more than 1 year in the past");
        }
    }

    private void validateCapacity(Cohort cohort, ValidationError error) {
        int maxStudents = cohort.getAssignment().maxStudents();
        if (maxStudents < MIN_STUDENTS || maxStudents > MAX_STUDENTS) {
            throw new ValidationException(error.code,
                String.format("Student capacity must be between %d and %d", MIN_STUDENTS, MAX_STUDENTS));
        }
    }

    private void validateSchoolAssignment(Cohort cohort, School school) {
        if (school == null || !school.isActive()) {
            throw new ValidationException(ValidationError.INVALID_SCHOOL.code,
                "Cohort must be assigned to an active school");
        }
        validateYearCapacity(cohort, school);
    }

    private void validateYearCapacity(Cohort cohort, School school) {
        long cohortsInYear = school.getCohorts().stream()
            .filter(c -> c.getAssignment().year().equals(cohort.getAssignment().year()))
            .count();

        if (MAX_COHORTS_PER_YEAR <= cohortsInYear) {
            throw new ValidationException(ValidationError.YEAR_CAPACITY_EXCEEDED.code,
                String.format("Maximum cohorts per year (%d) exceeded", CohortValidator.MAX_COHORTS_PER_YEAR));
        }
    }

    private void validateUniqueName(final Cohort cohort, final School school) {
        final boolean isDuplicate = school.getCohorts().stream()
            .filter(c -> !c.getId().equals(cohort.getId()))
            .anyMatch(c -> c.getName().equalsIgnoreCase(cohort.getName()));

        if (isDuplicate) {
            throw new ValidationException(ValidationError.DUPLICATE_NAME.code,
                "Cohort name must be unique within school");
        }
    }

    private void validateGradeLevel(final Cohort cohort) {
        final CohortAssignment assignment = cohort.getAssignment();
        if (null == assignment || null == assignment.level()) {
            throw new ValidationException("Cohort must have a valid grade level assigned");
        }

        if (!this.isValidGradeLevel(assignment.level())) {
            throw new ValidationException("Invalid grade level assigned");
        }
    }

    private void validateMaxStudents(final Cohort cohort) {
        if (MAX_STUDENTS < cohort.getAssignment().maxStudents()) {
            throw new ValidationException(
                String.format("Cohort exceeds maximum student capacity of %d", CohortValidator.MAX_STUDENTS));
        }
    }

    private void validateYearScheduling(final Cohort cohort, final School school) {
        final boolean hasOverlap = school.getCohorts().stream()
            .filter(c -> c.getAssignment().year().equals(cohort.getAssignment().year()))
            .filter(c -> !c.getId().equals(cohort.getId()))
            .anyMatch(c -> c.getAssignment().year().equals(cohort.getAssignment().year()));

        if (hasOverlap) {
            throw new ValidationException("Cohort schedule conflicts with existing cohort");
        }
    }

    private void validateStateTransition(final Cohort cohort) {
        if (cohort.isArchived() && cohort.isActive()) {
            throw new ValidationException(ValidationError.INVALID_STATE.code,
                "Archived cohort cannot be active");
        }
    }

    private boolean isValidGradeLevel(final GradeLevel level) {
        return null != level && !level.name().trim().isEmpty();
    }

    private enum ValidationError {
        INVALID_NAME(CohortValidator.ERROR_PREFIX + "001", "Invalid cohort name"),
        INVALID_DATES(CohortValidator.ERROR_PREFIX + "002", "Invalid term dates"),
        INVALID_CAPACITY(CohortValidator.ERROR_PREFIX + "003", "Invalid student capacity"),
        INVALID_SCHOOL(CohortValidator.ERROR_PREFIX + "004", "Invalid school assignment"),
        INVALID_STATE(CohortValidator.ERROR_PREFIX + "005", "Invalid state transition"),
        DUPLICATE_NAME(CohortValidator.ERROR_PREFIX + "006", "Duplicate cohort name"),
        YEAR_CAPACITY_EXCEEDED(CohortValidator.ERROR_PREFIX + "007", "Year capacity exceeded"),
        TEAM_LIMIT_EXCEEDED(CohortValidator.ERROR_PREFIX + "008", "Team limit exceeded"),
        ARCHIVED_MODIFICATION(CohortValidator.ERROR_PREFIX + "009", "Cannot modify archived cohort");

        final String code;
        final String message;

        ValidationError(final String code, final String message) {
            this.code = code;
            this.message = message;
        }
    }
}
