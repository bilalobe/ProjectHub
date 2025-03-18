package com.projecthub.base.project.api.dto;

import java.util.List;
import java.util.Map;

import com.projecthub.base.component.api.dto.ComponentDTO;
import com.projecthub.base.milestone.api.dto.MilestoneDTO;
import com.projecthub.base.task.api.dto.TaskDTO;

public record ProjectExportDTO(
    ProjectDTO project,
    List<ComponentDTO> components,
    List<TaskDTO> tasks,
    List<MilestoneDTO> milestones,
    Map<String, Object> metrics
) {}