package com.projecthub.base.milestone.application.port.in;

import com.projecthub.base.milestone.api.dto.MilestoneDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface LoadMilestoneUseCase {
    MilestoneDTO getMilestoneById(UUID id);

    List<MilestoneDTO> getAllMilestones();

    Page<MilestoneDTO> getAllMilestones(Pageable pageable);

    List<MilestoneDTO> getMilestonesByProject(UUID projectId);

    Page<MilestoneDTO> getMilestonesByProject(UUID projectId, Pageable pageable);
}
