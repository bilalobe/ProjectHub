package com.projecthub.base.student.api.controller;

import com.projecthub.base.student.api.dto.StudentDTO;
import com.projecthub.base.student.application.service.StudentCommandService;
import com.projecthub.base.student.domain.command.CreateStudentCommand;
import com.projecthub.base.student.domain.entity.Student;
import com.projecthub.base.team.domain.entity.Team;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentCommandService commandService;

    private Student testStudent;
    private Team testTeam;

    StudentControllerIntegrationTest() {
    }

    @BeforeEach
    void setUp() {
        testTeam = new Team();
        testTeam.setName("Test Team");

        CreateStudentCommand command = new CreateStudentCommand(
            "John",
            "Doe",
            "Middle",
            "john.doe@test.com",
            "+1234567890",
            "+1987654321",
            testTeam.getId(),
            UUID.randomUUID()
        );

        StudentDTO createdStudent = commandService.createStudent(command);
        testStudent = Student.builder()
            .firstName(createdStudent.firstName())
            .lastName(createdStudent.lastName())
            .email(createdStudent.email())
            .build();
    }

    @Test
    void getAllStudents_ShouldReturnStudentsList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(Matchers.greaterThanOrEqualTo(Integer.valueOf(1)))))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName", Matchers.notNullValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastName", Matchers.notNullValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].email", Matchers.notNullValue()));
    }

    @Test
    void getStudentById_WithValidId_ShouldReturnStudent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/students/{id}", Student.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(Student.getId().toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(testStudent.getFirstName()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(testStudent.getLastName()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(testStudent.getEmail()));
    }

    @Test
    void getStudentsByTeamId_WithValidTeamId_ShouldReturnStudentsList() throws Exception {
        mockMvc.perform(get("/api/v1/students/team/{teamId}", testTeam.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(Matchers.greaterThanOrEqualTo(Integer.valueOf(1)))))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].teamId").value(testTeam.getId().toString()));
    }

    @Test
    void createStudent_WithValidData_ShouldReturnCreatedStudent() throws Exception {
        String studentJson = """
            {
                "firstName": "Jane",
                "lastName": "Smith",
                "middleName": "Middle",
                "email": "jane.smith@test.com",
                "phoneNumber": "+1234567891",
                "emergencyContact": "+1987654322",
                "teamId": "%s"
            }
            """.formatted(testTeam.getId());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(studentJson))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Jane"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Smith"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("jane.smith@test.com"));
    }

    @Test
    void updateStudent_WithValidData_ShouldReturnUpdatedStudent() throws Exception {
        String updateJson = """
            {
                "firstName": "John",
                "lastName": "Updated",
                "middleName": "Middle",
                "email": "john.updated@test.com",
                "phoneNumber": "%s",
                "emergencyContact": "%s",
                "teamId": "%s"
            }
            """.formatted(testStudent.getPhoneNumber(), testStudent.getEmergencyContact(), testTeam.getId());

        mockMvc.perform(put("/api/v1/students/{id}", Student.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(Student.getId().toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Updated"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("john.updated@test.com"));
    }

    @Test
    void deleteStudent_WithValidId_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/students/{id}", Student.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void searchStudents_WithValidCriteria_ShouldReturnFilteredResults() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/students/search")
                .param("searchTerm", "John")
                .param("teamId", testTeam.getId().toString())
                .param("status", "ACTIVE")
                .param("enrolledAfter", LocalDate.now().minusDays(1).toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(Matchers.greaterThanOrEqualTo(Integer.valueOf(1)))))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].firstName", Matchers.containsStringIgnoringCase("john")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].teamId").value(testTeam.getId().toString()));
    }

    @Test
    void searchStudents_WithInvalidStatus_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/students/search")
                .param("status", "INVALID_STATUS")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void searchStudents_WithNoParameters_ShouldReturnAllStudents() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/students/search")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(Matchers.greaterThanOrEqualTo(Integer.valueOf(1)))));
    }
}
