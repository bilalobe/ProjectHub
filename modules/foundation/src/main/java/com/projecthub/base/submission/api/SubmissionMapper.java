package com.projecthub.base.submission.api;

import com.projecthub.base.submission.api.dto.SubmissionDTO;
import com.projecthub.base.submission.domain.entity.Submission;
import org.mapstruct.*;

/**
 * Mapper for Submission entity with protected relationship handling.
 */
@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubmissionMapper {

    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "student.id", source = "studentId")
    @Mapping(target = "id", ignore = true)
    Submission toEntity(SubmissionDTO dto);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "student.id", target = "studentId")
    SubmissionDTO toDto(Submission entity);

    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "student.id", source = "studentId")
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(SubmissionDTO dto, @MappingTarget Submission entity);
}