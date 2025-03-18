package com.projecthub.base.student.infrastructure.persistence.specification;

import com.projecthub.base.shared.domain.enums.status.ActivationStatus;
import com.projecthub.base.student.domain.entity.Student;
import com.projecthub.base.student.domain.repository.StudentJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@DataJpaTest
class StudentSpecificationTest {

    @Autowired
    private StudentJpaRepository repository;

    StudentSpecificationTest() {
    }

    @Test
    void shouldFilterByName() {
        // Given
        StudentSearchCriteria criteria = StudentSearchCriteria.builder()
            .nameSearch("John")
            .build();

        // When
        List<Student> results = repository.findAll(StudentSpecification.withDynamicQuery(criteria));

        // Then
        Assertions.assertThat(results).allMatch(student ->
            student.getFirstName().toLowerCase(Locale.ROOT).contains("john") ||
            student.getLastName().toLowerCase(Locale.ROOT).contains("john")
        );
    }

    @Test
    void shouldFilterByEmail() {
        // Given
        StudentSearchCriteria criteria = StudentSearchCriteria.builder()
            .email("test@example.com")
            .build();

        // When
        List<Student> results = repository.findAll(StudentSpecification.withDynamicQuery(criteria));

        // Then
        Assertions.assertThat(results).allMatch(student -> student.getEmail().equals("test@example.com"));
    }

    @Test
    void shouldFilterByTeamId() {
        // Given
        UUID teamId = UUID.randomUUID();
        StudentSearchCriteria criteria = StudentSearchCriteria.builder()
            .teamId(teamId)
            .build();

        // When
        List<Student> results = repository.findAll(StudentSpecification.withDynamicQuery(criteria));

        // Then
        Assertions.assertThat(results).allMatch(student -> student.getTeam() != null && student.getTeam().getId().equals(teamId));
    }

    @Test
    void shouldFilterByEnrollmentDateRange() {
        // Given
        LocalDate start = LocalDate.now().minusMonths(1L);
        LocalDate end = LocalDate.now();
        StudentSearchCriteria criteria = StudentSearchCriteria.builder()
            .enrollmentDateStart(start)
            .enrollmentDateEnd(end)
            .build();

        // When
        List<Student> results = repository.findAll(StudentSpecification.withDynamicQuery(criteria));

        // Then
        Assertions.assertThat(results).allMatch(student ->
            !student.getEnrollmentDate().isBefore(start) &&
            !student.getEnrollmentDate().isAfter(end)
        );
    }

    @Test
    void shouldCombineMultipleCriteria() {
        // Given
        UUID teamId = UUID.randomUUID();
        StudentSearchCriteria criteria = StudentSearchCriteria.builder()
            .nameSearch("John")
            .status(ActivationStatus.ACTIVE)
            .teamId(teamId)
            .build();

        // When
        Page<Student> results = repository.findAll(
            StudentSpecification.withDynamicQuery(criteria),
            PageRequest.of(0, 10)
        );

        // Then
        Assertions.assertThat(results.getContent()).allMatch(student ->
            (student.getFirstName().toLowerCase(Locale.ROOT).contains("john") ||
             student.getLastName().toLowerCase(Locale.ROOT).contains("john")) &&
            student.getStatus().equals(ActivationStatus.ACTIVE) &&
            student.getTeam() != null &&
            student.getTeam().getId().equals(teamId)
        );
    }
}
