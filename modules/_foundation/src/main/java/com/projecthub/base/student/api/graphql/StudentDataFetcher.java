package com.projecthub.base.student.api.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.projecthub.base.student.api.dto.StudentDTO;
import com.projecthub.base.student.application.service.StudentQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
@Slf4j
public class StudentDataFetcher {
    private final StudentQueryService queryService;

    @DgsQuery
    public StudentDTO student(@InputArgument String id) {
        log.debug("GraphQL query: Fetching student by ID {}", id);
        return queryService.getById(UUID.fromString(id));
    }

    @DgsQuery
    public List<StudentDTO> students() {
        log.debug("GraphQL query: Fetching all students");
        return queryService.getAllStudents();
    }

    @DgsQuery
    public List<StudentDTO> studentsByTeam(@InputArgument String teamId) {
        log.debug("GraphQL query: Fetching students by team ID {}", teamId);
        return queryService.getByTeamId(UUID.fromString(teamId));
    }
}