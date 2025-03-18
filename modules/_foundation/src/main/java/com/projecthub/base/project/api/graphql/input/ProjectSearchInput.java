package com.projecthub.base.project.api.graphql.input;

import com.projecthub.base.project.domain.enums.ProjectStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ProjectSearchInput {
    private String name;
    private ProjectStatus status;
    private UUID teamId;
    private LocalDate startDateFrom;
    private LocalDate startDateTo;
    private LocalDate deadlineFrom;
    private LocalDate deadlineTo;
    private String sortField;
    private boolean ascending = true;
}