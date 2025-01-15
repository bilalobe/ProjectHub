package com.projecthub.base.milestone.application.service;

import com.projecthub.base.milestone.api.dto.MilestoneDTO;
import com.projecthub.base.milestone.application.port.in.LoadMilestoneUseCase;
import com.projecthub.base.milestone.application.port.out.MilestonePort;
import com.projecthub.base.milestone.domain.entity.Milestone;
import com.projecthub.base.milestone.domain.exception.MilestoneNotFoundException;
import com.projecthub.base.milestone.infrastructure.mapper.MilestoneMapper;
import com.projecthub.base.milestone.infrastructure.specification.MilestoneSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MilestoneQueryService implements LoadMilestoneUseCase {
    private final MilestonePort milestonePort;
    private final MilestoneMapper milestoneMapper;

    @Override
    public MilestoneDTO getMilestoneById(final UUID id) {
        MilestoneQueryService.log.debug("Fetching milestone by ID: {}", id);
        return this.milestonePort.findById(id)
            .map(this.milestoneMapper::toDto)
            .orElseThrow(() -> new MilestoneNotFoundException("Milestone not found for id:" + id));
    }

    @Override
    public List<MilestoneDTO> getAllMilestones() {
        MilestoneQueryService.log.debug("Retrieving all milestones");
        return this.milestonePort.findAll().stream()
            .map(this.milestoneMapper::toDto)
            .toList();
    }

    @Override
    public Page<MilestoneDTO> getAllMilestones(final Pageable pageable) {
        MilestoneQueryService.log.debug("Fetching all milestones using pagination");
        return this.milestonePort.findAll(pageable)
            .map(this.milestoneMapper::toDto);
    }

    @Override
    public List<MilestoneDTO> getMilestonesByProject(final UUID projectId) {
        MilestoneQueryService.log.debug("Retrieving milestones for project ID: {}", projectId);
        return this.milestonePort.findAll(MilestoneSpecification.byProject(projectId)).stream()
            .map(this.milestoneMapper::toDto)
            .toList();
    }

    @Override
    public Page<MilestoneDTO> getMilestonesByProject(final UUID projectId, final Pageable pageable) {
        MilestoneQueryService.log.debug("Retrieving paginated milestones for project ID: {}", projectId);
        return this.milestonePort.findAll(MilestoneSpecification.byProject(projectId), pageable)
            .map(this.milestoneMapper::toDto);
    }

    private Milestone findMilestoneById(final UUID id) {
        return this.milestonePort.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Milestone not found with id " + id));
    }
}
