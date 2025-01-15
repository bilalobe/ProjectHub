package com.projecthub.base.submission.api.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.projecthub.base.submission.api.dto.SubmissionDTO;
import com.projecthub.base.submission.application.port.in.LoadSubmissionUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
@Slf4j
public class SubmissionDataFetcher {

    private final LoadSubmissionUseCase loadSubmissionUseCase;


    @DgsQuery
    public SubmissionDTO submission(@InputArgument String id) {
        log.debug("GraphQL query: Fetching submission by ID {}", id);
         return loadSubmissionUseCase.getSubmissionById(UUID.fromString(id));
    }

    @DgsQuery
    public List<SubmissionDTO> submissions() {
        log.debug("GraphQL query: Fetching all submissions");
         return loadSubmissionUseCase.getAllSubmissions();
    }

    @DgsQuery
    public Page<SubmissionDTO> allSubmissions(
            @InputArgument Integer page,
            @InputArgument Integer size) {
        log.debug("GraphQL query: Fetching all submissions using pagination");
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        return loadSubmissionUseCase.getAllSubmissions(pageable);
    }

    @DgsQuery
     public List<SubmissionDTO> submissionsByProject(@InputArgument String projectId) {
         log.debug("GraphQL query: Fetching all submissions by project {}", projectId);
         return loadSubmissionUseCase.getSubmissionsByProject(UUID.fromString(projectId));
    }
     @DgsQuery
    public Page<SubmissionDTO> submissionsByProject(
            @InputArgument String projectId,
            @InputArgument Integer page,
            @InputArgument Integer size) {
        log.debug("GraphQL query: Fetching paginated submissions by project {} with page {} and size {}", projectId, page, size);
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        return loadSubmissionUseCase.getSubmissionsByProject(UUID.fromString(projectId), pageable);
    }


    @DgsQuery
    public List<SubmissionDTO> submissionsByStudent(@InputArgument String studentId) {
         log.debug("GraphQL query: Fetching all submissions by student {}", studentId);
         return loadSubmissionUseCase.getSubmissionsByStudent(UUID.fromString(studentId));
    }

     @DgsQuery
      public Page<SubmissionDTO> submissionsByStudent(
            @InputArgument String studentId,
            @InputArgument Integer page,
            @InputArgument Integer size) {
           log.debug("GraphQL query: Fetching paginated submissions by student: {} with page: {} and size: {}", studentId, page, size);
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        return loadSubmissionUseCase.getSubmissionsByStudent(UUID.fromString(studentId), pageable);
    }
}