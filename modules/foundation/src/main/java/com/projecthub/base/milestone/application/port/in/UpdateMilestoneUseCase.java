
package com.projecthub.base.milestone.application.port.in;

import com.projecthub.base.milestone.domain.command.UpdateMilestoneCommand;
import com.projecthub.base.milestone.api.dto.MilestoneDTO;

public interface UpdateMilestoneUseCase {
    MilestoneDTO updateMilestone(UpdateMilestoneCommand command);
}
