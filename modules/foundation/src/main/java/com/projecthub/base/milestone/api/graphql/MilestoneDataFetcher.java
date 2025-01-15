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
    public MilestoneDTO milestone(@InputArgument final String id) {
        MilestoneDataFetcher.log.debug("GraphQL query: Fetching Milestone by id {}", id);
        return this.loadMilestoneUseCase.getMilestoneById(UUID.fromString(id));
    }

    @DgsQuery
    public List<MilestoneDTO> milestones() {
        MilestoneDataFetcher.log.debug("GraphQL query: Fetching all milestones");
        return this.loadMilestoneUseCase.getAllMilestones();
    }

    @DgsQuery
    public Page<MilestoneDTO> allMilestones(
        @InputArgument final Integer page,
        @InputArgument final Integer size) {
        MilestoneDataFetcher.log.debug("GraphQL query: Fetching all milestones using pagination");
        final Pageable pageable = PageRequest.of(null != page ? page : 0, null != size ? size : 10);
        return this.loadMilestoneUseCase.getAllMilestones(pageable);
    }

    @DgsQuery
    public List<MilestoneDTO> milestonesByProject(@InputArgument final String projectId) {
        MilestoneDataFetcher.log.debug("GraphQL query: Fetching milestones by project: {}", projectId);
        return this.loadMilestoneUseCase.getMilestonesByProject(UUID.fromString(projectId));
    }

    @DgsQuery
    public Page<MilestoneDTO> milestonesByProject(
        @InputArgument final String projectId,
        @InputArgument final Integer page,
        @InputArgument final Integer size
    ) {
        MilestoneDataFetcher.log.debug("GraphQL query: Fetching paginated milestones by project: {} using page: {} and size:{}", projectId, page, size);
        final Pageable pageable = PageRequest.of(null != page ? page : 0, null != size ? size : 10);
        return this.loadMilestoneUseCase.getMilestonesByProject(UUID.fromString(projectId), pageable);
    }
}
