package com.projecthub.base.submission.api.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import com.projecthub.base.submission.api.dto.SubmissionDTO;
import com.projecthub.base.submission.api.graphql.input.CreateSubmissionInput;
import com.projecthub.base.submission.api.graphql.input.UpdateSubmissionInput;
import com.projecthub.base.submission.api.mapper.SubmissionMapper;
import com.projecthub.base.submission.application.port.in.CreateSubmissionUseCase;
import com.projecthub.base.submission.application.port.in.DeleteSubmissionUseCase;
import com.projecthub.base.submission.application.port.in.UpdateSubmissionUseCase;
import com.projecthub.base.submission.domain.command.CreateSubmissionCommand;
import com.projecthub.base.submission.domain.command.DeleteSubmissionCommand;
import com.projecthub.base.submission.domain.command.UpdateSubmissionCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;

import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
@Slf4j
public class SubmissionMutationResolver {

    private final CreateSubmissionUseCase createSubmissionUseCase;
    private final UpdateSubmissionUseCase updateSubmissionUseCase;
    private final DeleteSubmissionUseCase deleteSubmissionUseCase;
    private final SubmissionMapper submissionMapper;


    @DgsMutation
    public SubmissionDTO createSubmission(@InputArgument CreateSubmissionInput input, Authentication authentication) {
        log.debug("GraphQL mutation: Creating submission: {}", input);
        CreateSubmissionCommand command = CreateSubmissionCommand.builder()
                .content(input.getContent())
                .projectId(input.getProjectId())
                .studentId(input.getStudentId())
                 .filePath(input.getFilePath())
                .initiatorId(UUID.randomUUID()) //TODO get initiator from auth context.
                .build();
        return createSubmissionUseCase.createSubmission(command);
    }

    @DgsMutation
    public SubmissionDTO updateSubmission(
            @InputArgument String id,
            @InputArgument UpdateSubmissionInput input,
             Authentication authentication
    ) {
        log.debug("GraphQL mutation: Updating submission with ID: {}", id);
       UpdateSubmissionCommand command = UpdateSubmissionCommand.builder()
           .submissionId(UUID.fromString(id))
             .content(input.getContent())
           .feedback(input.getFeedback())
          .grade(input.getGrade())
           .reviewerNotes(input.getReviewerNotes())
           .targetStatus(input.getTargetStatus())
           .filePath(input.getFilePath())
           .initiatorId(UUID.randomUUID()) //TODO get initiator from auth context.
            .build();

       return updateSubmissionUseCase.updateSubmission(command);
    }

   @DgsMutation
    public Boolean deleteSubmission(@InputArgument String id, Authentication authentication) {
        log.debug("GraphQL mutation: Deleting submission with ID: {}", id);
        deleteSubmissionUseCase.deleteSubmission(UUID.fromString(id),  UUID.randomUUID()); //TODO get initiator from auth context.
        return true;
    }
}