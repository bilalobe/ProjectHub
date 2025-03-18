package com.projecthub.base.submission.api.graphql;

import com.projecthub.base.submission.application.command.SubmissionCommand;
import com.projecthub.base.submission.application.service.SubmissionCommandService;
import com.projecthub.base.submission.domain.entity.Submission;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class SubmissionMutationResolver {
    private final SubmissionCommandService commandService;

    @MutationMapping
    public Submission createSubmission(
            @Argument CreateSubmissionInput input,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        SubmissionCommand.CreateSubmission command = new SubmissionCommand.CreateSubmission(
            input.getStudentId(),
            input.getProjectId(),
            input.getContent(),
            input.getFilePath(),
            input.isLate(),
            UUID.fromString(userDetails.getUsername())
        );

        return commandService.handleCreate(command);
    }

    @MutationMapping
    public Submission updateSubmission(
            @Argument UUID id,
            @Argument UpdateSubmissionInput input,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        SubmissionCommand.UpdateSubmission command = new SubmissionCommand.UpdateSubmission(
            id,
            input.getContent(),
            input.getFilePath(),
            UUID.fromString(userDetails.getUsername())
        );

        return commandService.handleUpdate(command);
    }

    @MutationMapping
    public Submission submitSubmission(
            @Argument UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        SubmissionCommand.SubmitSubmission command = new SubmissionCommand.SubmitSubmission(
            id,
            UUID.fromString(userDetails.getUsername())
        );

        return commandService.handleSubmit(command);
    }

    @MutationMapping
    public Submission gradeSubmission(
            @Argument UUID id,
            @Argument GradeSubmissionInput input,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        SubmissionCommand.GradeSubmission command = new SubmissionCommand.GradeSubmission(
            id,
            input.getGrade(),
            input.getFeedback(),
            UUID.fromString(userDetails.getUsername())
        );

        return commandService.handleGrade(command);
    }

    @MutationMapping
    public Submission revokeSubmission(
            @Argument UUID id,
            @Argument String reason,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        SubmissionCommand.RevokeSubmission command = new SubmissionCommand.RevokeSubmission(
            id,
            reason,
            UUID.fromString(userDetails.getUsername())
        );

        return commandService.handleRevoke(command);
    }

    @MutationMapping
    public Submission addComment(
            @Argument UUID submissionId,
            @Argument String text,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        SubmissionCommand.AddComment command = new SubmissionCommand.AddComment(
            submissionId,
            text,
            UUID.fromString(userDetails.getUsername())
        );

        return commandService.handleAddComment(command);
    }
}
