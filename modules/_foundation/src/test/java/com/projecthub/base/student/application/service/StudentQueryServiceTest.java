package com.projecthub.base.student.application.service;

import com.projecthub.base.shared.domain.enums.status.ActivationStatus;
import com.projecthub.base.student.api.dto.StudentDTO;
import com.projecthub.base.student.api.dto.StudentSearchCriteria;
import com.projecthub.base.student.api.mapper.StudentMapper;
import com.projecthub.base.student.domain.entity.Student;
import com.projecthub.base.student.domain.repository.StudentJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class StudentQueryServiceTest {

    @Mock
    private StudentJpaRepository repository;

    @Mock
    private StudentMapper mapper;

    @InjectMocks
    private StudentQueryService queryService;

    private Student testStudent;
    private StudentDTO testStudentDTO;
    private UUID testTeamId;

    StudentQueryServiceTest() {
    }

    @BeforeEach
    void setUp() {
        testTeamId = UUID.randomUUID();
        testStudent = Student.builder()
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@test.com")
            .build();
        testStudent.setStatus(ActivationStatus.ACTIVE);

        testStudentDTO = new StudentDTO(
            Student.getId(),
            testStudent.getFirstName(),
            testStudent.getLastName(),
            testStudent.getMiddleName(),
            testStudent.getEmail(),
            testStudent.getPhoneNumber(),
            testStudent.getEmergencyContact(),
            testTeamId,
            ActivationStatus.ACTIVE,
            testStudent.getEnrollmentDate()
        );
    }

    @Test
    void searchStudents_WithValidCriteria_ShouldReturnFilteredResults() {
        // Arrange
        StudentSearchCriteria criteria = StudentSearchCriteria.builder()
            .searchTerm("John")
            .teamId(testTeamId)
            .status(ActivationStatus.ACTIVE)
            .enrolledAfter(LocalDate.now().minusDays(1L))
            .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Student> studentPage = new PageImpl<>(List.of(testStudent));

        Mockito.when(repository.findAll(ArgumentMatchers.any(Specification.class), ArgumentMatchers.any(Pageable.class)))
            .thenAnswer(invocation -> studentPage);
        Mockito.when(mapper.toDto(testStudent)).thenReturn(testStudentDTO);

        // Act
        Page<StudentDTO> result = queryService.searchStudents(criteria, pageable);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getContent())
            .hasSize(1)
            .first()
            .satisfies(dto -> {
                Assertions.assertThat(dto.firstName()).isEqualTo("John");
                Assertions.assertThat(dto.lastName()).isEqualTo("Doe");
                Assertions.assertThat(dto.email()).isEqualTo("john.doe@test.com");
                Assertions.assertThat(dto.teamId()).isEqualTo(testTeamId);
                Assertions.assertThat(dto.status()).isEqualTo(ActivationStatus.ACTIVE);
            });
    }

    @Test
    void searchStudents_WithEmptyCriteria_ShouldReturnAllStudents() {
        // Arrange
        StudentSearchCriteria criteria = StudentSearchCriteria.builder().build();
        List<Student> students = List.of(testStudent);

        Mockito.when(repository.findAll(ArgumentMatchers.any(Specification.class)))
            .thenAnswer(invocation -> students);
        Mockito.when(mapper.toDto(testStudent)).thenReturn(testStudentDTO);

        // Act
        List<StudentDTO> result = queryService.searchStudents(criteria);

        // Assert
        Assertions.assertThat(result)
            .hasSize(1)
            .first()
            .satisfies(dto -> {
                Assertions.assertThat(dto.firstName()).isEqualTo("John");
                Assertions.assertThat(dto.lastName()).isEqualTo("Doe");
                Assertions.assertThat(dto.email()).isEqualTo("john.doe@test.com");
            });
    }

    @Test
    void searchStudents_WithNullCriteria_ShouldReturnEmptyResults() {
        // Arrange
        Mockito.when(repository.findAll(ArgumentMatchers.any(Specification.class)))
            .thenAnswer(invocation -> List.of());

        // Act
        List<StudentDTO> result = queryService.searchStudents(null);

        // Assert
        Assertions.assertThat(result).isEmpty();
    }
}
