package com.projecthub.base.submission.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projecthub.base.submission.api.dto.*;
import com.projecthub.base.submission.application.command.SubmissionCommand;
import com.projecthub.base.submission.application.service.SubmissionBatchService;
import com.projecthub.base.submission.application.service.SubmissionCommandService;
import com.projecthub.base.submission.application.service.SubmissionQueryService;
import com.projecthub.base.submission.domain.entity.Submission;
import com.projecthub.base.submission.domain.enums.SubmissionStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Submission End-to-End Tests")
class SubmissionEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SubmissionCommandService commandService;

    @Autowired
    private SubmissionQueryService queryService;

    @Autowired
    private SubmissionBatchService batchService;

    SubmissionEndToEndTest() {
    }

    @Test
    @Transactional
    @WithMockUser(username = "student1", roles = "STUDENT")
    @DisplayName("Should complete full submission workflow successfully")
    void shouldCompleteFullSubmissionWorkflowSuccessfully() throws Exception, com.fasterxml.jackson.core.JsonProcessingException, com.fasterxml.jackson.databind.JsonMappingException, java.io.UnsupportedEncodingException {
        // Create submission
        CreateSubmissionRequest createRequest = new CreateSubmissionRequest(
            UUID.fromString("student1"),
            UUID.randomUUID(),
            "Test submission content",
            "test.pdf",
            false
        );

        MvcResult createResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/submissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("DRAFT"))
            .andReturn();

        SubmissionResponse submission = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            SubmissionResponse.class
        );

        // Update submission
        UpdateSubmissionRequest updateRequest = new UpdateSubmissionRequest(
            "Updated content",
            "updated.pdf"
        );

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/submissions/" + submission.submissionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content").value("Updated content"));

        // Submit submission
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/submissions/" + submission.submissionId() + "/submit"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUBMITTED"));

        // Switch to instructor role for grading
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/submissions/" + submission.submissionId() + "/grade")
                .with(request -> {
                    request.setRemoteUser("instructor1");
                    return request;
                })
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new GradeSubmissionRequest(85, "Good work!")
                )))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("GRADED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.grade").value(Integer.valueOf(85)));

        // Add comment
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/submissions/" + submission.submissionId() + "/comments")
                .param("text", "Great improvement!"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.comments[0].text").value("Great improvement!"));

        // Verify final state
        Submission finalState = queryService.findById(submission.submissionId()).orElseThrow();
        Assertions.assertThat(finalState.getStatus()).isEqualTo(SubmissionStatus.GRADED);
        Assertions.assertThat(finalState.getGrade()).isEqualTo(85);
        assertThat(finalState.getComments()).hasSize(1);
    }

    @Test
    @Transactional
    @WithMockUser(username = "instructor1", roles = "INSTRUCTOR")
    @DisplayName("Should handle batch operations successfully")
    void shouldHandleBatchOperationsSuccessfully() throws Exception, com.fasterxml.jackson.core.JsonProcessingException {
        // Create test submissions
        UUID projectId = UUID.randomUUID();
        Submission sub1 = createTestSubmission(projectId);
        Submission sub2 = createTestSubmission(projectId);

        // Batch grade submissions
        BatchGradeRequest batchGradeRequest = new BatchGradeRequest(
            Set.of(sub1.getSubmissionId(), sub2.getSubmissionId()),
            90,
            "Excellent work!"
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/submissions/batch/grade")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(batchGradeRequest)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].grade").value(Integer.valueOf(90)))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].grade").value(Integer.valueOf(90)));

        // Verify analytics
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/submissions/analytics/project/" + projectId + "/stats"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.gradedSubmissions").value(Integer.valueOf(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.averageGrade").value(Double.valueOf(90.0)));

        // Export results
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/submissions/export/project/" + projectId + "/excel"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.header().exists("Content-Disposition"));
    }

    private Submission createTestSubmission(UUID projectId) {
        return commandService.handleCreate(new SubmissionCommand.CreateSubmission(
            UUID.randomUUID(),
            projectId,
            "Test content",
            "test.txt",
            false,
            UUID.fromString("instructor1")
        ));
    }
}
