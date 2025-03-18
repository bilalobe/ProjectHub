package com.projecthub.base.project.api.graphql.input;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ProjectInput {
    private String name;
    private String description;
    private UUID teamId;
    private LocalDate startDate;
    private LocalDate deadline;
}