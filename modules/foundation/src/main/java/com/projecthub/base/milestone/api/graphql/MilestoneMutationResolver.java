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
import com.projecthub.base.milestone.domain.command.DeleteMilestoneCommand;
import com.projecthub.base.milestone.domain.command.UpdateMilestoneCommand;
import com.projecthub.base.milestone.domain.value.MilestoneValue;
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
    public MilestoneDTO createMilestone(@InputArgument CreateMilestoneInput input, Authentication authentication) {
        log.debug("GraphQL mutation: Creating milestone {}", input.getName());
        var command = CreateMilestoneCommand.builder()
            .milestoneDetails(milestoneMapper.toValue(input))
            .initiatorId(UUID.randomUUID()) //TODO use authenticated user
            .build();
        return createMilestoneUseCase.createMilestone(command);
    }

    @DgsMutation
    public MilestoneDTO updateMilestone(
        @InputArgument String id,
        @InputArgument UpdateMilestoneInput input,
        Authentication authentication) {
        log.debug("GraphQL mutation: Updating milestone {}", id);
        UpdateMilestoneCommand command = UpdateMilestoneCommand.builder()
            .id(UUID.fromString(id))
            .milestoneDetails(milestoneMapper.toValue(input))
            .initiatorId(UUID.randomUUID()) //TODO use authenticated user
            .targetStatus(input.getStatus())
            .build();
        return updateMilestoneUseCase.updateMilestone(command);
    }


    @DgsMutation
    public Boolean deleteMilestone(@InputArgument String id, Authentication authentication) {
        log.debug("GraphQL mutation: Deleting milestone: {}", id);
        deleteMilestoneUseCase.deleteMilestone(
            UUID.fromString(id),
            UUID.randomUUID()//TODO use authenticated user
        );
        return true;
    }
}
