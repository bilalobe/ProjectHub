
package com.projecthub.base.milestone.application.port.in;

import java.util.UUID;

public interface DeleteMilestoneUseCase {
    void deleteMilestone(UUID id, UUID initiatorId);
}
