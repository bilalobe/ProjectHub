package com.projecthub.base.milestone.domain.specification;

import com.projecthub.base.milestone.domain.entity.Milestone;
import com.projecthub.base.milestone.domain.enums.MilestoneStatus;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public enum MilestoneSpecification {
    ;
    private static final String DUE_DATE = "dueDate";
    private static final String PROJECT = "project";
    private static final String STATUS = "status";
    private static final String PROGRESS = "progress";
    private static final String ID = "id";

    public static Specification<Milestone> byProject(final UUID projectId) {
        return (root, _, cb) -> cb.equal(root.get(MilestoneSpecification.PROJECT).get(MilestoneSpecification.ID), projectId);
    }

    public static Specification<Milestone> byStatus(final MilestoneStatus status) {
        return (root, _, cb) -> cb.equal(root.get(MilestoneSpecification.STATUS), status);
    }

    public static Specification<Milestone> dueBefore(final LocalDate date) {
        return (root, _, cb) -> cb.lessThan(root.get(MilestoneSpecification.DUE_DATE), date);
    }

    public static Specification<Milestone> dueAfter(final LocalDate date) {
        return (root, _, cb) -> cb.greaterThan(root.get(MilestoneSpecification.DUE_DATE), date);
    }

    public static Specification<Milestone> overdue() {
        return (root, _, cb) -> cb.and(
            cb.lessThan(root.get(MilestoneSpecification.DUE_DATE), LocalDate.now()),
            cb.notEqual(root.get(MilestoneSpecification.STATUS), MilestoneStatus.COMPLETED)
        );
    }

    public static Specification<Milestone> hasProgress(final int minProgress) {
        return (root, _, cb) -> cb.greaterThanOrEqualTo(root.get(MilestoneSpecification.PROGRESS), minProgress);
    }

    public static Specification<Milestone> withActiveDependencies() {
        return (root, _, cb) -> {
            final Join<Milestone, Milestone> dependencies = root.join("dependencies");
            return cb.equal(dependencies.get(MilestoneSpecification.STATUS), MilestoneStatus.IN_PROGRESS);
        };
    }

    public static Specification<Milestone> withCompletedDependencies() {
        return (root, _, cb) -> {
            final Join<Milestone, Milestone> dependencies = root.join("dependencies");
            return cb.equal(dependencies.get(MilestoneSpecification.STATUS), MilestoneStatus.COMPLETED);
        };
    }
}
