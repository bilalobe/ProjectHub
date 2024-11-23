package com.projecthub.mapper;

import com.projecthub.dto.SubmissionSummary;
import com.projecthub.model.Submission;
import com.projecthub.model.Project;
import com.projecthub.model.Student;

public class SubmissionMapper {

    public static Submission toSubmission(SubmissionSummary submissionSummary, Project project, Student student) {
        Submission submission = new Submission();
        submission.setId(submissionSummary.getId());
        submission.setContent(submissionSummary.getContent());
        submission.setTimestamp(submissionSummary.getTimestamp());
        submission.setGrade(submissionSummary.getGrade());
        submission.setProject(project);
        submission.setStudent(student);
        return submission;
    }

    public static SubmissionSummary toSubmissionSummary(Submission submission) {
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