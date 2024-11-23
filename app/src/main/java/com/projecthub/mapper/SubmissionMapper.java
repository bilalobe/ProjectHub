package com.projecthub.mapper;

import com.projecthub.dto.SubmissionSummary;
import com.projecthub.model.Project;
import com.projecthub.model.Student;
import com.projecthub.model.Submission;

public class SubmissionMapper {

    public static Submission toSubmission(SubmissionSummary submissionSummary, Project project, Student student) {
        if (submissionSummary == null) {
            return null;
        }
        Submission submission = new Submission(
            student,
            project,
            submissionSummary.getContent(),
            submissionSummary.getTimestamp()
        );
        return submission;
    }

    public static SubmissionSummary toSubmissionSummary(Submission submission) {
        if (submission == null) {
            return null;
        }
        return new SubmissionSummary(
            submission.getId(),
            submission.getContent(),
            submission.getTimestamp(),
            submission.getGrade(),
            submission.getProject() != null ? submission.getProject().getId() : null,
            submission.getStudent() != null ? submission.getStudent().getId() : null
        );
    }
}