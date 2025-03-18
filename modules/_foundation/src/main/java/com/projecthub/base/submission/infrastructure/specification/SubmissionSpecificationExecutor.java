package com.projecthub.base.submission.infrastructure.specification;

import com.projecthub.base.submission.domain.entity.Submission;
import org.springframework.data.jpa.domain.Specification;

public class SubmissionSpecificationExecutor {

    public SubmissionSpecificationExecutor() {
    }

    public static Specification<Submission> withCriteria(SubmissionSearchCriteria criteria) {
        return Specification.where(SubmissionSpecification.byStudent(criteria.studentId()))
            .and(SubmissionSpecification.byProject(criteria.projectId()))
            .and(SubmissionSpecification.hasStatus(criteria.status()))
            .and(SubmissionSpecification.submittedAfter(criteria.submittedAfter()))
            .and(SubmissionSpecification.submittedBefore(criteria.submittedBefore()))
            .and(SubmissionSpecification.hasMinimumGrade(criteria.minGrade()))
            .and(SubmissionSpecification.hasMaximumGrade(criteria.maxGrade()))
            .and(SubmissionSpecification.isLate(criteria.isLate()))
            .and(SubmissionSpecification.hasComments(criteria.hasComments()));
    }
}
