package com.projecthub.mapper;

import com.projecthub.dto.ProjectSummary;
import com.projecthub.dto.SubmissionSummary;
import com.projecthub.model.Project;
import com.projecthub.model.Student;
import com.projecthub.model.Submission;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between Submission entities and SubmissionSummary DTOs.
 */
@Component
public class SubmissionMapper {

    /**
     * Converts a SubmissionSummary DTO to a Submission entity.
     *
     * @param submissionSummary the SubmissionSummary DTO
     * @param project the associated Project entity
     * @param student the associated Student entity
     * @return the converted Submission entity
     */
    public Submission toSubmission(SubmissionSummary submissionSummary, Project project, Student student) {
        if (submissionSummary == null) {
            return null;
        }
        Submission submission = new Submission(
            submissionSummary.getId(),
            student,
            project,
            submissionSummary.getContent(),
            submissionSummary.getTimestamp(),
            submissionSummary.getGrade()
        );
        submission.setId(submissionSummary.getId());
        submission.setContent(submissionSummary.getContent());
        submission.setTimestamp(submissionSummary.getTimestamp());
        submission.setGrade(submissionSummary.getGrade());
        submission.setProject(project);
        submission.setStudent(student);
        return submission;
    }

    /**
     * Converts a Submission entity to a SubmissionSummary DTO.
     *
     * @param submission the Submission entity
     * @return the converted SubmissionSummary DTO
     */
    public SubmissionSummary toSubmissionSummary(Submission submission) {
        if (submission == null) {
            return null;
        }
        return new SubmissionSummary(
            submission.getId(),
            submission.getContent(),
            submission.getTimestamp(),
            submission.getGrade(),
            submission.getProject() != null ? submission.getProject().getId() : null,
            submission.getStudent() != null ? submission.getStudent().getId() : null,
            submission.getProject() != null ? submission.getProject().getName() : null,
            submission.getStudent() != null ? submission.getStudent().getName() : null
        );
    }

    /**
     * Converts a Project entity to a ProjectSummary DTO.
     *
     * @param project the Project entity
     * @return the converted ProjectSummary DTO
     */
    public ProjectSummary toProjectSummary(Project project) {
        if (project == null) {
            return null;
        }
        return new ProjectSummary(
            project.getId(),
            project.getName(),
            project.getDescription(),
            project.getTeam() != null ? project.getTeam().getId() : null,
            project.getDeadline(),
            project.getStartDate(),
            project.getEndDate(),
            project.getStatus()
        );
    }
}