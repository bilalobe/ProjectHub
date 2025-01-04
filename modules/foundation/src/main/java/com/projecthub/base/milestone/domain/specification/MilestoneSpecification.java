package com.projecthub.base.milestone.domain.specification;

import com.projecthub.base.milestone.domain.entity.Milestone;
import com.projecthub.base.milestone.domain.enums.MilestoneStatus;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public final class MilestoneSpecification {
    private static final String DUE_DATE = "dueDate";
    private static final String PROJECT = "project";
    private static final String STATUS = "status";
    private static final String PROGRESS = "progress";
    private static final String ID = "id";

    private MilestoneSpecification() {
    }

    public static Specification<Milestone> byProject(UUID projectId) {
        return (root, _, cb) -> cb.equal(root.get(PROJECT).get(ID), projectId);
    }

    public static Specification<Milestone> byStatus(MilestoneStatus status) {
        return (root, _, cb) -> cb.equal(root.get(STATUS), status);
    }

    public static Specification<Milestone> dueBefore(LocalDate date) {
        return (root, _, cb) -> cb.lessThan(root.get(DUE_DATE), date);
    }

    public static Specification<Milestone> dueAfter(LocalDate date) {
        return (root, _, cb) -> cb.greaterThan(root.get(DUE_DATE), date);
    }

    public static Specification<Milestone> overdue() {
        return (root, _, cb) -> cb.and(
            cb.lessThan(root.get(DUE_DATE), LocalDate.now()),
            cb.notEqual(root.get(STATUS), MilestoneStatus.COMPLETED)
        );
    }

    public static Specification<Milestone> hasProgress(int minProgress) {
        return (root, _, cb) -> cb.greaterThanOrEqualTo(root.get(PROGRESS), minProgress);
    }

    public static Specification<Milestone> withActiveDependencies() {
        return (root, _, cb) -> {
            Join<Milestone, Milestone> dependencies = root.join("dependencies");
            return cb.equal(dependencies.get(STATUS), MilestoneStatus.IN_PROGRESS);
        };
    }

    public static Specification<Milestone> withCompletedDependencies() {
        return (root, _, cb) -> {
            Join<Milestone, Milestone> dependencies = root.join("dependencies");
            return cb.equal(dependencies.get(STATUS), MilestoneStatus.COMPLETED);
        };
    }
}
