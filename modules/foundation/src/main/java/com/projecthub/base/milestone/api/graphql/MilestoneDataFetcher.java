package com.projecthub.base.milestone.api.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.projecthub.base.milestone.api.dto.MilestoneDTO;
import com.projecthub.base.milestone.application.port.in.LoadMilestoneUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
@Slf4j
public class MilestoneDataFetcher {
    private final LoadMilestoneUseCase loadMilestoneUseCase;


    @DgsQuery
    public MilestoneDTO milestone(@InputArgument String id) {
        log.debug("GraphQL query: Fetching Milestone by id {}", id);
        return loadMilestoneUseCase.getMilestoneById(UUID.fromString(id));
    }

    @DgsQuery
    public List<MilestoneDTO> milestones() {
        log.debug("GraphQL query: Fetching all milestones");
        return loadMilestoneUseCase.getAllMilestones();
    }

    @DgsQuery
    public Page<MilestoneDTO> allMilestones(
        @InputArgument Integer page,
        @InputArgument Integer size) {
        log.debug("GraphQL query: Fetching all milestones using pagination");
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        return loadMilestoneUseCase.getAllMilestones(pageable);
    }

    @DgsQuery
    public List<MilestoneDTO> milestonesByProject(@InputArgument String projectId) {
        log.debug("GraphQL query: Fetching milestones by project: {}", projectId);
        return loadMilestoneUseCase.getMilestonesByProject(UUID.fromString(projectId));
    }

    @DgsQuery
    public Page<MilestoneDTO> milestonesByProject(
        @InputArgument String projectId,
        @InputArgument Integer page,
        @InputArgument Integer size
    ) {
        log.debug("GraphQL query: Fetching paginated milestones by project: {} using page: {} and size:{}", projectId, page, size);
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        return loadMilestoneUseCase.getMilestonesByProject(UUID.fromString(projectId), pageable);
    }
}
