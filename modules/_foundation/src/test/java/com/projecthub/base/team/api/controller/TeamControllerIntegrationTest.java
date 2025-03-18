package com.projecthub.base.team.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projecthub.base.team.api.dto.TeamDTO;
import com.projecthub.base.team.application.service.TeamService;
import com.projecthub.base.team.infrastructure.security.TeamSecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

@WebMvcTest(TeamController.class)
class TeamControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    @MockBean
    private TeamSecurityService securityService;

    private UUID testTeamId;
    private TeamDTO teamDTO;

    TeamControllerIntegrationTest() {
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();

        testTeamId = UUID.randomUUID();
        teamDTO = new TeamDTO();
        teamDTO.setId(testTeamId);
        teamDTO.setName("Test Team");
        teamDTO.setDescription("Test Team Description");
    }

    @Test
    @WithMockUser(username = "user", authorities = {"team:read"})
    void getAllTeams_withReadPermission_returnsOk() throws Exception {
        Mockito.when(teamService.getAllTeams()).thenReturn(List.of(teamDTO));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/teams")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "user", authorities = {})
    void getAllTeams_withoutReadPermission_returnsForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/teams")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"team:read"})
    void getTeamById_withReadPermission_returnsOk() throws Exception {
        Mockito.when(teamService.getById(ArgumentMatchers.eq(testTeamId))).thenReturn(teamDTO);
        Mockito.doNothing().when(securityService).enforceReadPermission(ArgumentMatchers.eq(testTeamId));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/teams/{id}", testTeamId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"team:read"})
    void getTeamById_whenSecurityCheckFails_returnsForbidden() throws Exception {
        Mockito.doThrow(new AccessDeniedException("Access denied")).when(securityService).enforceReadPermission(ArgumentMatchers.eq(testTeamId));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/teams/{id}", testTeamId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"team:create"})
    void createTeam_withCreatePermission_returnsOk() throws Exception, com.fasterxml.jackson.core.JsonProcessingException {
        Mockito.when(teamService.createTeam(ArgumentMatchers.any(TeamDTO.class))).thenReturn(teamDTO);
        Mockito.doNothing().when(securityService).enforceCreatePermission();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teamDTO)))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"team:create"})
    void createTeam_whenSecurityCheckFails_returnsForbidden() throws Exception, com.fasterxml.jackson.core.JsonProcessingException {
        Mockito.doThrow(new AccessDeniedException("Access denied")).when(securityService).enforceCreatePermission();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teamDTO)))
            .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
