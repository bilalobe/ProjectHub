package com.projecthub.core.mappers;

import com.projecthub.core.dto.SubmissionDTO;
import com.projecthub.core.models.Submission;
import org.mapstruct.*;

/**
 * Mapper for Submission entity with protected relationship handling.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubmissionMapper extends BaseMapper<SubmissionDTO, Submission> {

    @Override
    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "student.id", source = "studentId")
    @Mapping(target = "id", ignore = true)
    Submission toEntity(SubmissionDTO dto);

    @Override
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "student.id", target = "studentId")
    SubmissionDTO toDto(Submission entity);

    @Override
    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "student.id", source = "studentId")
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(SubmissionDTO dto, @MappingTarget Submission entity);
}