package com.projecthub.base.submission.api.graphql;

import com.projecthub.base.submission.application.service.SubmissionQueryService;
import com.projecthub.base.submission.domain.entity.Submission;
import com.projecthub.base.submission.infrastructure.specification.SubmissionSearchCriteria;
import com.projecthub.base.submission.infrastructure.specification.SubmissionSpecificationExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class SubmissionQueryResolver {
    private final SubmissionQueryService queryService;

    @QueryMapping
    public Optional<Submission> submission(@Argument UUID id) {
        return queryService.findById(id);
    }

    @QueryMapping
    public List<Submission> submissions(@Argument SubmissionSearchCriteria criteria) {
        return queryService.findWithCriteria(
            SubmissionSpecificationExecutor.withCriteria(criteria)
        );
    }

    @QueryMapping
    public List<Submission> studentSubmissions(@Argument UUID studentId) {
        return queryService.findByStudentId(studentId);
    }

    @QueryMapping
    public List<Submission> projectSubmissions(@Argument UUID projectId) {
        return queryService.findByProjectId(projectId);
    }

    @QueryMapping
    public List<Submission> pendingSubmissions(@Argument UUID projectId) {
        return queryService.findPendingGradingByProjectId(projectId);
    }
}