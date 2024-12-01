package com.projecthub.mapper;

import com.projecthub.dto.SubmissionSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.model.Project;
import com.projecthub.model.Student;
import com.projecthub.model.Submission;
import com.projecthub.repository.ProjectRepository;
import com.projecthub.repository.StudentRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between Submission entities and SubmissionSummary DTOs.
 */
@Component
public class SubmissionMapper {

    private final ProjectRepository projectRepository;
    private final StudentRepository studentRepository;

    public SubmissionMapper(ProjectRepository projectRepository, StudentRepository studentRepository) {
        this.projectRepository = projectRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * Converts a SubmissionSummary DTO to a Submission entity.
     *
     * @param submissionSummary the SubmissionSummary DTO
     * @return the converted Submission entity
     */
    public Submission toSubmission(SubmissionSummary submissionSummary) {
        if (submissionSummary == null) {
            return null;
        }

        Project project = projectRepository.findById(submissionSummary.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + submissionSummary.getProjectId()));
        Student student = studentRepository.findById(submissionSummary.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + submissionSummary.getStudentId()));

        Submission submission = new Submission(null, student, project, null, null, null);
        submission.setProject(project);
        submission.setStudent(student);
        submission.setContent(submissionSummary.getContent());
        submission.setTimestamp(submissionSummary.getTimestamp());
        submission.setGrade(submissionSummary.getGrade());
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
            submission.getStudent() != null ? submission.getStudent().getUsername() : null
        );
    }

    /**
     * Updates a Submission entity from a SubmissionSummary DTO.
     *
     * @param submissionSummary the SubmissionSummary DTO
     * @param submission the Submission entity
     */
    public void updateSubmissionFromSummary(SubmissionSummary submissionSummary, Submission submission) {
        if (submissionSummary == null || submission == null) {
            return;
        }

        if (submissionSummary.getProjectId() != null) {
            Project project = projectRepository.findById(submissionSummary.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + submissionSummary.getProjectId()));
            submission.setProject(project);
        }

        if (submissionSummary.getStudentId() != null) {
            Student student = studentRepository.findById(submissionSummary.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + submissionSummary.getStudentId()));
            submission.setStudent(student);
        }

        submission.setContent(submissionSummary.getContent());
        submission.setTimestamp(submissionSummary.getTimestamp());
        submission.setGrade(submissionSummary.getGrade());
    }
}