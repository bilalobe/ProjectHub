package com.projecthub.mapper;

import com.projecthub.dto.SubmissionDTO;
import com.projecthub.model.Submission;
import com.projecthub.ui.viewmodels.details.SubmissionDetailsViewModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {ProjectMapper.class, StudentMapper.class})
public interface SubmissionMapper {

    SubmissionMapper INSTANCE = Mappers.getMapper(SubmissionMapper.class);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "studentId", target = "student.id")
    @Mapping(target = "filePath", ignore = true) // Assuming filePath is managed separately
    @Mapping(target = "createdAt", ignore = true) // Assuming createdAt is managed separately
    @Mapping(target = "updatedAt", ignore = true) // Assuming updatedAt is managed separately
    @Mapping(target = "createdBy", ignore = true) // Assuming createdBy is managed separately
    @Mapping(target = "deleted", ignore = true) // Assuming deleted is managed separately
    Submission toSubmission(SubmissionDTO submissionDTO);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "studentFirstName", source = "student.firstName")
    @Mapping(target = "studentLastName", source = "student.lastName")
    SubmissionDTO toSubmissionDTO(Submission submission);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "studentId", target = "student.id")
    @Mapping(target = "filePath", ignore = true) // Assuming filePath is managed separately
    @Mapping(target = "createdAt", ignore = true) // Assuming createdAt is managed separately
    @Mapping(target = "updatedAt", ignore = true) // Assuming updatedAt is managed separately
    @Mapping(target = "createdBy", ignore = true) // Assuming createdBy is managed separately
    @Mapping(target = "deleted", ignore = true) // Assuming deleted is managed separately
    void updateSubmissionFromDTO(SubmissionDTO submissionDTO, @MappingTarget Submission submission);

    // New method to convert SubmissionDetailsViewModel to SubmissionDTO
    @Mapping(source = "submissionId", target = "id")
    @Mapping(source = "projectId", target = "projectId")
    @Mapping(source = "studentId", target = "studentId")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "timestamp", target = "timestamp")
    @Mapping(source = "grade", target = "grade")
    @Mapping(source = "projectName", target = "projectName")
    @Mapping(source = "studentFirstName", target = "studentFirstName")
    @Mapping(source = "studentLastName", target = "studentLastName")
    SubmissionDTO toSubmissionDTO(SubmissionDetailsViewModel viewModel);
}