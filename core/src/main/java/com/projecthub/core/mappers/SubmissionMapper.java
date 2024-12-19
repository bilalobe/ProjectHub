package com.projecthub.core.mappers;

import com.projecthub.core.dto.SubmissionDTO;
import com.projecthub.core.models.Submission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {

    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "student.id", source = "studentId")
    Submission toSubmission(SubmissionDTO submissionDTO);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "student.id", target = "studentId")
    SubmissionDTO toSubmissionDTO(Submission submission);

    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "student.id", source = "studentId")
    void updateSubmissionFromDTO(SubmissionDTO submissionDTO, @MappingTarget Submission submission);
}