package com.projecthub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for project status data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStatusDTO {

    private String status;
    private int count;
}