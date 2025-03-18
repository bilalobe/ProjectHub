package com.projecthub.base.submission.api.mapper;

import com.projecthub.base.submission.api.dto.SubmissionResponse;
import com.projecthub.base.submission.api.dto.SubmissionResponse.CommentResponse;
import com.projecthub.base.submission.domain.entity.Submission;
import com.projecthub.base.submission.domain.value.Comment;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Component
public class SubmissionMapper {

    public SubmissionMapper() {
    }

    public SubmissionResponse toResponse(Submission submission) {
        return new SubmissionResponse(
            submission.getSubmissionId(),
            submission.getStudentId(),
            submission.getProjectId(),
            submission.getContent(),
            submission.getFilePath(),
            submission.getGrade(),
            submission.getFeedback(),
            submission.getStatus(),
            submission.isLate(),
            submission.getSubmittedAt(),
            mapComments(submission.getComments()),
            Instant.from(submission.getCreatedDate()),
            Instant.from(submission.getLastModifiedDate())
        );
    }

    private static List<CommentResponse> mapComments(Collection<Comment> comments) {
        return comments.stream()
            .map(comment -> new CommentResponse(
                comment.getCommentId(),
                comment.getText(),
                comment.getAuthorId(),
                comment.getCreatedAt()
            ))
            .toList();
    }
}
