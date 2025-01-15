package com.projecthub.base.milestone.application.port.in;


import com.projecthub.base.milestone.api.dto.MilestoneDTO;
import com.projecthub.base.milestone.domain.command.CreateMilestoneCommand;


public interface CreateMilestoneUseCase {
    MilestoneDTO createMilestone(CreateMilestoneCommand command);
}
