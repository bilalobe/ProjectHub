package com.projecthub.base.milestone.domain.validation;

import com.projecthub.base.milestone.domain.entity.Milestone;

import java.util.Set;


public interface MilestoneValidation {

    void validateCreate(Milestone milestone);

    void validateUpdate(Milestone milestone);

    void validateDelete(Milestone milestone);

    void validateMilestones(Set<Milestone> milestones);

}
