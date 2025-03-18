package com.projecthub.base.submission.infrastructure.specification;

import com.projecthub.base.submission.domain.enums.SubmissionStatus;

import java.time.Instant;
import java.util.UUID;

public record SubmissionSearchCriteria(
    UUID studentId,
    UUID projectId,
    SubmissionStatus status,
    Instant submittedAfter,
    Instant submittedBefore,
    Integer minGrade,
    Integer maxGrade,
    Boolean isLate,
    Boolean hasComments
) {
    public static SubmissionSearchCriteriaBuilder builder() {
        return new SubmissionSearchCriteriaBuilder();
    }

    public static class SubmissionSearchCriteriaBuilder {
        private UUID studentId = null;
        private UUID projectId = null;
        private SubmissionStatus status = null;
        private Instant submittedAfter = null;
        private Instant submittedBefore = null;
        private Integer minGrade = null;
        private Integer maxGrade = null;
        private Boolean isLate = null;
        private Boolean hasComments = null;

        public SubmissionSearchCriteriaBuilder() {
        }

        public SubmissionSearchCriteriaBuilder studentId(UUID studentId) {
            this.studentId = studentId;
            return this;
        }

        public SubmissionSearchCriteriaBuilder projectId(UUID projectId) {
            this.projectId = projectId;
            return this;
        }

        public SubmissionSearchCriteriaBuilder status(SubmissionStatus status) {
            this.status = status;
            return this;
        }

        public SubmissionSearchCriteriaBuilder submittedAfter(Instant submittedAfter) {
            this.submittedAfter = submittedAfter;
            return this;
        }

        public SubmissionSearchCriteriaBuilder submittedBefore(Instant submittedBefore) {
            this.submittedBefore = submittedBefore;
            return this;
        }

        public SubmissionSearchCriteriaBuilder minGrade(Integer minGrade) {
            this.minGrade = minGrade;
            return this;
        }

        public SubmissionSearchCriteriaBuilder maxGrade(Integer maxGrade) {
            this.maxGrade = maxGrade;
            return this;
        }

        public SubmissionSearchCriteriaBuilder isLate(Boolean isLate) {
            this.isLate = isLate;
            return this;
        }

        public SubmissionSearchCriteriaBuilder hasComments(Boolean hasComments) {
            this.hasComments = hasComments;
            return this;
        }

        public SubmissionSearchCriteria build() {
            return new SubmissionSearchCriteria(
                studentId, projectId, status, submittedAfter, submittedBefore,
                minGrade, maxGrade, isLate, hasComments
            );
        }
    }
}
