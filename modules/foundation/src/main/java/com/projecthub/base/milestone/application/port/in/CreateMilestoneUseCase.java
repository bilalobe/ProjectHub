package com.projecthub.base.milestone.application.port.in;


import com.projecthub.base.milestone.domain.command.CreateMilestoneCommand;
import com.projecthub.base.milestone.api.dto.MilestoneDTO;
import java.util.UUID;


public interface CreateMilestoneUseCase {
    MilestoneDTO createMilestone(CreateMilestoneCommand command);
}
