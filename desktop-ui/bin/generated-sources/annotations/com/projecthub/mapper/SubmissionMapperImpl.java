package com.projecthub.mapper;

import com.projecthub.dto.SubmissionDTO;
import com.projecthub.model.Project;
import com.projecthub.model.Student;
import com.projecthub.model.Submission;
import com.projecthub.ui.viewmodels.details.SubmissionDetailsViewModel;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;

import org.springframework.stereotype.Component;

@Generated(
        value = "org.mapstruct.ap.MappingProcessor",
        date = "2024-12-08T14:18:23+0100",
        comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.40.0.z20241112-1021, environment: Java 17.0.13 (Eclipse Adoptium)"
)
@Component
public class SubmissionMapperImpl implements SubmissionMapper {

    @Override
    public Submission toSubmission(SubmissionDTO submissionDTO) {
        if (submissionDTO == null) {
            return null;
        }

        Submission submission = new Submission();

        submission.setProject(submissionDTOToProject(submissionDTO));
        submission.setStudent(submissionDTOToStudent(submissionDTO));
        submission.setId(submissionDTO.getId());
        submission.setContent(submissionDTO.getContent());
        submission.setTimestamp(submissionDTO.getTimestamp());
        submission.setGrade(submissionDTO.getGrade());

        return submission;
    }

    @Override
    public SubmissionDTO toSubmissionDTO(Submission submission) {
        if (submission == null) {
            return null;
        }

        SubmissionDTO submissionDTO = new SubmissionDTO();

        submissionDTO.setProjectId(submissionProjectId(submission));
        submissionDTO.setStudentId(submissionStudentId(submission));
        submissionDTO.setProjectName(submissionProjectName(submission));
        submissionDTO.setStudentFirstName(submissionStudentFirstName(submission));
        submissionDTO.setStudentLastName(submissionStudentLastName(submission));
        submissionDTO.setId(submission.getId());
        submissionDTO.setContent(submission.getContent());
        submissionDTO.setTimestamp(submission.getTimestamp());
        submissionDTO.setGrade(submission.getGrade());

        return submissionDTO;
    }

    @Override
    public void updateSubmissionFromDTO(SubmissionDTO submissionDTO, Submission submission) {
        if (submissionDTO == null) {
            return;
        }

        if (submission.getProject() == null) {
            submission.setProject(new Project());
        }
        submissionDTOToProject1(submissionDTO, submission.getProject());
        if (submission.getStudent() == null) {
            submission.setStudent(new Student());
        }
        submissionDTOToStudent1(submissionDTO, submission.getStudent());
        submission.setId(submissionDTO.getId());
        submission.setContent(submissionDTO.getContent());
        submission.setTimestamp(submissionDTO.getTimestamp());
        submission.setGrade(submissionDTO.getGrade());
    }

    @Override
    public SubmissionDTO toSubmissionDTO(SubmissionDetailsViewModel viewModel) {
        if (viewModel == null) {
            return null;
        }

        SubmissionDTO submissionDTO = new SubmissionDTO();

        submissionDTO.setId(viewModel.getSubmissionId());
        submissionDTO.setProjectId(viewModel.getProjectId());
        submissionDTO.setStudentId(viewModel.getStudentId());
        submissionDTO.setContent(viewModel.getContent());
        if (viewModel.getTimestamp() != null) {
            submissionDTO.setTimestamp(LocalDateTime.parse(viewModel.getTimestamp()));
        }
        if (viewModel.getGrade() != null) {
            submissionDTO.setGrade(Integer.parseInt(viewModel.getGrade()));
        }
        submissionDTO.setProjectName(viewModel.getProjectName());
        submissionDTO.setStudentFirstName(viewModel.getStudentFirstName());
        submissionDTO.setStudentLastName(viewModel.getStudentLastName());

        return submissionDTO;
    }

    protected Project submissionDTOToProject(SubmissionDTO submissionDTO) {
        if (submissionDTO == null) {
            return null;
        }

        Project project = new Project();

        project.setId(submissionDTO.getProjectId());

        return project;
    }

    protected Student submissionDTOToStudent(SubmissionDTO submissionDTO) {
        if (submissionDTO == null) {
            return null;
        }

        Student student = new Student();

        student.setId(submissionDTO.getStudentId());

        return student;
    }

    private UUID submissionProjectId(Submission submission) {
        Project project = submission.getProject();
        if (project == null) {
            return null;
        }
        return project.getId();
    }

    private UUID submissionStudentId(Submission submission) {
        Student student = submission.getStudent();
        if (student == null) {
            return null;
        }
        return student.getId();
    }

    private String submissionProjectName(Submission submission) {
        Project project = submission.getProject();
        if (project == null) {
            return null;
        }
        return project.getName();
    }

    private String submissionStudentFirstName(Submission submission) {
        Student student = submission.getStudent();
        if (student == null) {
            return null;
        }
        return student.getFirstName();
    }

    private String submissionStudentLastName(Submission submission) {
        Student student = submission.getStudent();
        if (student == null) {
            return null;
        }
        return student.getLastName();
    }

    protected void submissionDTOToProject1(SubmissionDTO submissionDTO, Project mappingTarget) {
        if (submissionDTO == null) {
            return;
        }

        mappingTarget.setId(submissionDTO.getProjectId());
    }

    protected void submissionDTOToStudent1(SubmissionDTO submissionDTO, Student mappingTarget) {
        if (submissionDTO == null) {
            return;
        }

        mappingTarget.setId(submissionDTO.getStudentId());
    }
}
