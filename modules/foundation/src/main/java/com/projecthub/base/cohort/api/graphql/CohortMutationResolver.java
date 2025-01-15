package com.projecthub.base.cohort.api.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import com.projecthub.base.cohort.api.dto.CohortDTO;
import com.projecthub.base.cohort.api.exception.GraphQLApiException;
import com.projecthub.base.cohort.api.input.CreateCohortInput;
import com.projecthub.base.cohort.api.input.UpdateCohortInput;
import com.projecthub.base.cohort.api.mapper.CohortMapper;
import com.projecthub.base.cohort.application.service.CohortCommandService;
import com.projecthub.base.cohort.domain.command.AssignTeamToCohortCommand;
import com.projecthub.base.cohort.domain.command.CompleteCohortCommand;
import com.projecthub.base.cohort.domain.command.DeleteCohortCommand;
import com.projecthub.base.team.application.service.TeamCommandService;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
public class CohortMutationResolver {
    private final CohortCommandService commandService;
    private final TeamCommandService teamCommandService;
    private final CohortMapper cohortMapper;

    @DgsMutation
    public CohortDTO createCohort(@InputArgument final CreateCohortInput input) {
        try {
            final var command = this.cohortMapper.toCreateCommand(input);
            return this.commandService.createCohort(command);
        } catch (final Exception e) {
            throw new GraphQLApiException(e.getMessage(), "CREATE_COHORT_ERROR");
        }
    }

    @DgsMutation
    public CohortDTO updateCohort(
        @InputArgument final String id,
        @InputArgument final UpdateCohortInput input
    ) {
        final var command = this.cohortMapper.toUpdateCommand(input, UUID.fromString(id));
        return this.commandService.updateCohort(command);
    }

    @DgsMutation
    public Boolean deleteCohort(
        @InputArgument final String id,
        @InputArgument final String reason
    ) {
        try {
            this.commandService.deleteCohort(
                DeleteCohortCommand.builder()
                    .id(UUID.fromString(id))
                    .reason(reason)
                    .build()
            );
            return true;
        } catch (final Exception e) {
            throw new GraphQLApiException(e.getMessage(), "DELETE_COHORT_ERROR");
        }
    }

    @DgsMutation
    public Boolean completeCohort(@InputArgument final String id) {
        try {
            this.commandService.completeCohort(
                CompleteCohortCommand.builder()
                    .id(UUID.fromString(id))
                    .build()
            );
            return true;
        } catch (final Exception e) {
            throw new GraphQLApiException(e.getMessage(), "COMPLETE_COHORT_ERROR");
        }
    }

    @DgsMutation
    public Boolean assignTeamToCohort(
        @InputArgument final String teamId,
        @InputArgument final String cohortId
    ) {
        try {
            this.teamCommandService.assignToCohort(
                AssignTeamToCohortCommand.builder()
                    .teamId(UUID.fromString(teamId))
                    .cohortId(UUID.fromString(cohortId))
                    .build()
            );
            return true;
        } catch (final Exception e) {
            throw new GraphQLApiException(e.getMessage(), "ASSIGN_TEAM_ERROR");
        }
    }
}
