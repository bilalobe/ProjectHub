package com.projecthub.base.milestone.api.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import com.projecthub.base.milestone.api.dto.MilestoneDTO;
import com.projecthub.base.milestone.api.graphql.input.CreateMilestoneInput;
import com.projecthub.base.milestone.api.graphql.input.UpdateMilestoneInput;
import com.projecthub.base.milestone.application.port.in.CreateMilestoneUseCase;
import com.projecthub.base.milestone.application.port.in.DeleteMilestoneUseCase;
import com.projecthub.base.milestone.application.port.in.UpdateMilestoneUseCase;
import com.projecthub.base.milestone.domain.command.CreateMilestoneCommand;
import com.projecthub.base.milestone.domain.command.UpdateMilestoneCommand;
import com.projecthub.base.milestone.infrastructure.mapper.MilestoneMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;

import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
@Slf4j
public class MilestoneMutationResolver {

    private final CreateMilestoneUseCase createMilestoneUseCase;
    private final UpdateMilestoneUseCase updateMilestoneUseCase;
    private final DeleteMilestoneUseCase deleteMilestoneUseCase;
    private final MilestoneMapper milestoneMapper;

    @DgsMutation
    public MilestoneDTO createMilestone(@InputArgument final CreateMilestoneInput input, final Authentication authentication) {
        MilestoneMutationResolver.log.debug("GraphQL mutation: Creating milestone {}", input.getName());
        final var command = CreateMilestoneCommand.builder()
            .milestoneDetails(this.milestoneMapper.toValue(input))
            .initiatorId(UUID.randomUUID()) //TODO use authenticated user
            .build();
        return this.createMilestoneUseCase.createMilestone(command);
    }

    @DgsMutation
    public MilestoneDTO updateMilestone(
        @InputArgument final String id,
        @InputArgument final UpdateMilestoneInput input,
        final Authentication authentication) {
        MilestoneMutationResolver.log.debug("GraphQL mutation: Updating milestone {}", id);
        final UpdateMilestoneCommand command = UpdateMilestoneCommand.builder()
            .id(UUID.fromString(id))
            .milestoneDetails(this.milestoneMapper.toValue(input))
            .initiatorId(UUID.randomUUID()) //TODO use authenticated user
            .targetStatus(input.getStatus())
            .build();
        return this.updateMilestoneUseCase.updateMilestone(command);
    }


    @DgsMutation
    public Boolean deleteMilestone(@InputArgument final String id, final Authentication authentication) {
        MilestoneMutationResolver.log.debug("GraphQL mutation: Deleting milestone: {}", id);
        this.deleteMilestoneUseCase.deleteMilestone(
            UUID.fromString(id),
            UUID.randomUUID()//TODO use authenticated user
        );
        return true;
    }
}
